---
name: ai-docs-sync
description: 从当前对话或最近1天历史会话中提取软件工程领域知识，生成待确认的文档草稿到 docs/07-ai-knowledge-inbox/。手动调用时分析当前对话，传入 cron 参数时切换为定时任务模式（读取历史 JSONL + git 变更）。
allowed-tools: Read, Glob, Grep, Write, Bash
argument-hint: [cron]
---

# AI Docs Sync

根据 `$ARGUMENTS` 判断运行模式：
- 无参数 或 `manual`：**手动模式**，分析当前对话上下文
- 参数为 `cron`：**定时任务模式**，读取最近1天历史会话 + git 变更

---

## Step 1：获取信息来源

### 手动模式
直接分析当前对话上下文中的所有消息内容，无需额外读取文件。

### 定时任务模式

运行以下命令获取原始信息：

**获取最近1天的历史会话内容：**
```
!`python3 -c "
import os, json, glob
from datetime import datetime, timedelta, timezone

project_key = os.getcwd().replace('/', '-')
pattern = os.path.expanduser(f'~/.claude/projects/{project_key}/*.jsonl')
cutoff = datetime.now(timezone.utc) - timedelta(days=1)
messages = []

for f in glob.glob(pattern):
    with open(f) as fp:
        for line in fp:
            try:
                obj = json.loads(line)
                ts = obj.get('timestamp', '')
                if not ts:
                    continue
                dt = datetime.fromisoformat(ts.replace('Z', '+00:00'))
                if dt < cutoff:
                    continue
                msg = obj.get('message', {})
                role = msg.get('role', '')
                content = msg.get('content', '')
                if role not in ('user', 'assistant'):
                    continue
                if isinstance(content, list):
                    text = ' '.join(c.get('text','') for c in content if isinstance(c,dict) and c.get('type')=='text')
                elif isinstance(content, str):
                    text = content
                else:
                    continue
                text = text.strip()
                if not text or text.startswith('<') or len(text) < 10:
                    continue
                messages.append(f'[{role.upper()} @ {ts[:16]}] {text[:800]}')
            except:
                pass

print('\n---\n'.join(messages[-60:]) if messages else '（最近1天无有效会话记录）')
"`
```

**获取最近1天 git 变更摘要（基于主分支，非当前分支）：**
```
!`MAIN_BRANCH=$(git remote show origin 2>/dev/null | grep 'HEAD branch' | awk '{print $NF}'); MAIN_BRANCH=${MAIN_BRANCH:-develop}; git log origin/$MAIN_BRANCH --since="24 hours ago" --oneline --no-merges 2>/dev/null || echo "（无最近提交）"`
```
```
!`MAIN_BRANCH=$(git remote show origin 2>/dev/null | grep 'HEAD branch' | awk '{print $NF}'); MAIN_BRANCH=${MAIN_BRANCH:-develop}; git diff origin/$MAIN_BRANCH~3...origin/$MAIN_BRANCH --stat 2>/dev/null | head -40 || echo "（无变更统计）"`
```

---

## Step 2：扫描现有 docs 结构

读取 docs 目录，了解已有文档，避免重复生成：

```
!`find docs -type f -name "*.md" | sort`
```

对已有文档，按需选择性读取相关文件内容，判断是否存在可追加的章节。

---

## Step 3：提取 SE 领域知识

从获取的信息中，**仅提取具有文档沉淀价值的软件工程知识**，忽略以下内容：
- 纯操作性指令（"帮我看一下这个文件"）
- 调试过程中的临时排查步骤
- 已经体现在代码中、无需文档说明的细节

**提取维度（对应 docs 目录）：**

| 知识类型 | 目标目录 | 示例 |
|---------|---------|------|
| 业务需求、功能决策、产品边界 | `01-requirements/` | 确认了某功能的业务规则 |
| 技术选型、架构决策、系统设计 | `02-architecture/` | 选择了 Nacos 替代 K8s ConfigMap 管理业务配置 |
| 接口设计、数据模型、流程设计 | `03-design/` | 定义了某 API 的请求/响应结构 |
| 测试策略、测试用例约定 | `04-test/` | 集成测试不依赖 MySQL/Redis 容器 |
| 发布流程、版本管理约定 | `05-release/` | 发布前必须执行的检查项 |
| 部署方案、配置项说明、运维规范 | `06-ops/` | 环境变量 NACOS_ADDR 的说明 |

---

## Step 4：判断每条知识的文档操作

对每条提取出的知识，判断：

1. **归属目录**：按上表映射到 `01~06` 中的某个目录
2. **操作类型**：
   - `NEW_FILE`：该目录下没有合适的已有文档
   - `MODIFY`：已有某文件，需要在特定章节追加或修改内容

---

## Step 5：生成草稿文件

生成时间戳：`!`date +%Y-%m-%d-%H%M``

在 `docs/07-ai-knowledge-inbox/` 下创建文件，命名格式：`{timestamp}-{slug}.md`

每个草稿文件包含 **一个或多个知识条目**，格式如下：

```markdown
# AI 知识草稿

- **来源**：[manual|cron] / {日期}
- **状态**：待确认 (Draft)

---

## 条目 1：{知识点标题}

- **操作类型**：NEW_FILE
- **目标路径**：`docs/02-architecture/nacos-vs-k8s-config.md`
- **标签**：#架构 #配置管理 #Nacos

### 内容草稿

{此处填写 AI 生成的知识内容，结构清晰，可直接用于正式文档}

---

## 条目 2：{知识点标题}

- **操作类型**：MODIFY
- **目标文件**：`docs/06-ops/README.md`
- **目标章节**：`## 配置文件说明`（如章节不存在则新增）
- **标签**：#运维 #环境变量

### 追加内容

{此处填写需要追加或替换的具体内容}

---

## 确认意见

- [ ] 已审核
- [ ] 需修正（请在下方补充意见）
- [ ] 已通过 /ai-docs-apply 应用到正式文档

> 修正意见：
```

---

## Step 6：定时任务模式的 git 操作

**仅在 cron 模式下执行：**

```bash
# 保存当前分支名
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# 切换或创建 ai-knowledge-inbox 分支
git checkout ai-knowledge-inbox 2>/dev/null || git checkout -b ai-knowledge-inbox

# 暂存并提交新生成的草稿文件
git add docs/07-ai-knowledge-inbox/
git commit -m "ai: auto knowledge draft $(date +%Y-%m-%d)"

# 推送到远端
git push origin ai-knowledge-inbox

# 切回原分支
git checkout $CURRENT_BRANCH
```

执行上述操作时，如果遇到冲突或错误，报告错误信息，不要强制操作。

---

## 完成后输出

输出简洁摘要：
- 提取到多少条知识
- 生成了哪个草稿文件（文件路径）
- 每条知识的操作类型和目标位置
- （cron 模式）git 操作结果

如果没有发现任何值得沉淀的 SE 知识，说明原因并静默退出（cron 模式下不创建空文件）。
