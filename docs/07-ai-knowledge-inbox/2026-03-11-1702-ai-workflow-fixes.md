# AI 知识草稿

- **来源**：cron / 2026-03-11
- **状态**：待确认 (Draft)

---

## 条目 1：修正 project-key 路径描述（ai-knowledge-sync.md）

- **操作类型**：MODIFY
- **目标文件**：`docs/06-ops/ai-knowledge-sync.md`
- **目标章节**：`## 会话历史读取`
- **标签**：#运维 #bugfix #会话历史

### 追加内容

将第35行错误描述：
> 其中 `project-key` 是项目绝对路径，将 `/` 替换为 `-` 并去掉开头的 `-`。

修正为：
> 其中 `project-key` 是项目绝对路径，将 `/` 替换为 `-`（**保留**开头的 `-`，不做 lstrip）。

例如：`/Users/bc/code/common/ProjectTemplate` → `-Users-bc-code-common-ProjectTemplate`（目录名以 `-` 开头）

> **背景**：曾出现 `lstrip('-')` 导致匹配不到正确目录的 bug，根因是 Python 的 `lstrip` 会移除所有开头的 `-` 字符，而实际目录名 `-Users-...` 需要保留这个前导 `-`。

---

## 条目 2：Skill 文件应纳入 git 追踪

- **操作类型**：MODIFY
- **目标文件**：`docs/06-ops/ai-knowledge-sync.md`
- **目标章节**：`## Git 分支策略`（追加新小节）
- **标签**：#运维 #skill #git

### 追加内容

在 `## Git 分支策略` 章节末尾追加：

```markdown
## Skill 文件管理

`.claude/skills/` 目录下的 skill 文件**必须纳入 git 追踪**，否则在切换分支时会丢失（未追踪文件在某些 git 操作下会被清理）。

```bash
git add .claude/skills/
git commit -m "chore: track skill files in git"
```

Skill 文件应随项目代码一起版本管理，和 CLAUDE.md 同等对待。
```

---

## 条目 3：更新 ai-docs-apply 工作流描述

- **操作类型**：MODIFY
- **目标文件**：`docs/02-architecture/ai-knowledge-workflow.md`
- **目标章节**：`### 两阶段操作`
- **标签**：#架构 #ai-docs-apply #工作流

### 追加内容

将 `### 两阶段操作` 流程图更新为：

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

---

## 条目 4：Maven 依赖治理规范

- **操作类型**：NEW_FILE
- **目标路径**：`docs/03-design/maven-dependency-governance.md`
- **标签**：#设计 #Maven #依赖管理

### 内容草稿

<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1702-ai-workflow-fixes.md -->

# Maven 依赖治理规范

## 背景

本项目经历了一次 `pom.xml` 重构（commit `6cf813f`），清理了冗余依赖与版本管理问题，沉淀出以下规范。

## 版本管理原则

1. **所有第三方依赖版本统一在父 `pom.xml` 的 `<dependencyManagement>` 中声明**，子模块只引用 groupId + artifactId，不写版本号。
2. **Spring Boot BOM / Spring Cloud BOM 通过 `import` scope 引入**，避免在各子模块重复声明版本。
3. **避免在子模块 `pom.xml` 中覆盖父模块已管理的版本**，除非有明确理由（需注释说明）。

## 依赖分层规则

参见模块架构的依赖规则：下层模块不得依赖上层模块。

| 模块 | 允许依赖 |
|------|---------|
| `template-common` | 无内部依赖 |
| `template-api` | common |
| `template-dao` | common |
| `template-service` | dao、common |
| `template-client` | api、common |
| `template-biz` | service、client、common |
| `template-web` | 所有模块 |

## 清理规范

- 定期检查 `pom.xml` 中的 `<exclusion>`，确保排除原因仍然有效
- 删除未使用的 `<dependency>` 声明（IDE 或 `mvn dependency:analyze` 可辅助检测）
- `pom.xml.backup` / `pom.xml.bak` 等备份文件不应提交到 git（已加入 `.gitignore`）

---

## 确认意见

- [ ] 已审核
- [ ] 需修正（请在下方补充意见）
- [ ] 已通过 /ai-docs-apply 应用到正式文档

> 修正意见：
