<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1447-ai-knowledge-workflow.md -->

# AI 知识管理工作流

## 背景

项目开发过程中，与 AI（Claude Code）的对话会产生大量有价值的软件工程知识，包括需求决策、架构选型、运维规范等。这些知识容易随对话结束而丢失，因此建立了一套自动化的知识沉淀机制。

## 工作流设计

### 两阶段操作

```
阶段一：知识提取（ai-docs-sync）
  ↓ 分析对话或历史记录
  ↓ 提取 SE 领域知识条目
  ↓ 生成草稿文件到 docs/07-ai-knowledge-inbox/
  ↓ 人工审核

阶段二：知识应用（ai-docs-apply）
  ↓ 读取已确认草稿
  ↓ 执行新建文件 or 修改已有文件
  ↓ 草稿文件直接删除（历史追溯看 git log）
  ↓ commit 变更 + 自动检测 GitHub/GitLab 创建 PR/MR 到 main/master 分支
```

**变更说明：**
- 草稿应用后**直接删除**，不再保留带完成标记的文件（`07-ai-knowledge-inbox` 作为收件箱，处理完的文件没有保留价值）
- `ai-docs-apply` 执行后自动创建 PR/MR，目标分支为 `main` 或 `master`，自动检测 GitHub（`gh pr create`）或 GitLab（`glab mr create`）环境

### 两种触发模式

| 模式 | 触发方式 | 信息来源 | 输出位置 | Git 操作 |
|------|---------|---------|---------|---------|
| 手动 | `/ai-docs-sync` | 当前对话上下文 | 当前分支 `docs/07-ai-knowledge-inbox/` | 无，由开发者决定 |
| 定时 | 每天 08:47 自动 | 最近1天 JSONL + git log | `ai-knowledge-inbox` 分支 | 自动 commit + push |

### 知识分类映射

| 知识类型 | 目标目录 |
|---------|---------|
| 业务需求、功能决策 | `01-requirements/` |
| 技术选型、架构决策 | `02-architecture/` |
| 接口、数据模型、流程设计 | `03-design/` |
| 测试策略、用例约定 | `04-test/` |
| 发布流程、版本约定 | `05-release/` |
| 部署、配置、运维规范 | `06-ops/` |

## 草稿文件规范

- **存放位置**：`docs/07-ai-knowledge-inbox/`
- **命名格式**：`{YYYY-MM-DD-HHmm}-{slug}.md`
- **每个文件**可包含多个知识条目，每条条目标注操作类型（NEW_FILE / MODIFY）和目标位置

## 相关工具

- Skill 文件：`.claude/skills/ai-docs-sync/SKILL.md`
- Skill 文件：`.claude/skills/ai-docs-apply/SKILL.md`
- 会话历史位置：`~/.claude/projects/<project-key>/*.jsonl`

## ai-docs-apply 完整工作流

`/ai-docs-apply` 在应用草稿后会自动执行以下操作：

1. **应用文档变更**：NEW_FILE 新建 / MODIFY 修改目标文件
2. **删除草稿文件**：收件箱保持干净，历史通过 git log 追溯
3. **Git commit**：`docs: apply ai knowledge draft {draft-name}`
4. **自动检测平台并创建 PR/MR**：
   - 通过 `git remote get-url origin` 判断平台
   - URL 含 `github.com` → 使用 `gh pr create`，目标分支为默认主分支
   - 其他（GitLab 或私有域名）→ 使用 `glab mr create`，目标分支为默认主分支
   - 若 CLI 工具未安装，跳过 PR/MR 创建，只执行 commit + push
