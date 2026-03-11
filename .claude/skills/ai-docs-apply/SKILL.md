---
name: ai-docs-apply
description: 将 docs/07-ai-knowledge-inbox/ 下已确认的草稿文件应用到正式文档。读取草稿中的操作指令（新建文件或修改已有文件），执行实际变更，删除草稿，提交并创建 PR/MR 到主分支。
allowed-tools: Read, Glob, Grep, Write, Edit, Bash
argument-hint: <draft-filename>
---

# AI Docs Apply

将 `docs/07-ai-knowledge-inbox/` 下的草稿文件应用到正式文档。

**调用方式：**
- `/ai-docs-apply 2026-03-11-1430-nacos-config.md` — 应用指定草稿
- `/ai-docs-apply` — 列出所有待确认的草稿，让用户选择

---

## Step 1：确认目标文件

### 有参数时
目标草稿文件为：`docs/07-ai-knowledge-inbox/$ARGUMENTS`

读取该文件，确认其存在且状态为"待确认 (Draft)"（即 `- [ ] 已通过 /ai-docs-apply 应用到正式文档` 未勾选）。

若文件不存在或已标记完成，告知用户并退出。

### 无参数时

列出所有待确认草稿：
```
!`find docs/07-ai-knowledge-inbox -name "*.md" ! -name "README.md" ! -name ".knowledge-template.md" | sort`
```

读取每个文件的前20行，找出状态为 Draft（`- [ ] 已通过`）的文件，列出供用户选择，然后停止等待用户指定文件名后再继续。

---

## Step 2：解析草稿内容

读取草稿文件，解析所有"条目"，每个条目包含：

- **操作类型**：`NEW_FILE` 或 `MODIFY`
- **目标路径 / 目标文件**
- **目标章节**（MODIFY 时）
- **内容草稿 / 追加内容**

跳过"确认意见"中标注"需修正"或有未完成修正意见的条目，并告知用户。

---

## Step 3：逐条执行文档操作

### NEW_FILE 操作

1. 检查目标路径的父目录是否存在，不存在则创建
2. 检查目标文件是否已存在：
   - 不存在：直接创建，写入内容草稿
   - 已存在：询问用户是追加到文件末尾，还是跳过
3. 创建文件后在文件头部添加标准元数据注释（创建日期、来源草稿文件名）

### MODIFY 操作

1. 读取目标文件
2. 定位目标章节（按标题匹配）：
   - 找到章节：在该章节末尾追加内容
   - 未找到章节：在文件末尾新增该章节标题，然后写入内容
3. 使用 Edit 工具精确修改，不影响文件其他部分

---

## Step 4：删除草稿文件

所有条目处理完毕后，直接删除草稿文件：

```bash
rm docs/07-ai-knowledge-inbox/{filename}
```

历史追溯通过 git log 查看即可，收件箱保持干净。

---

## Step 5：提交变更并创建 PR/MR

### 5.1 Git commit

将所有变更（新建/修改的正式文档 + 删除的草稿文件）一并提交：

```bash
git add -A
git commit -m "docs: apply ai knowledge draft {draft-filename-without-ext}"
```

### 5.2 检测平台

通过 remote URL 判断平台：

```bash
git remote get-url origin
```

- URL 含 `github.com` → **GitHub**，使用 `gh` CLI
- 其他（含 `gitlab`、私有域名等）→ **GitLab**，使用 `glab` CLI

同时获取默认主分支名：

```bash
git remote show origin | grep 'HEAD branch' | awk '{print $NF}'
```

### 5.3 推送当前分支

```bash
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
git push origin $CURRENT_BRANCH
```

### 5.4 创建 PR/MR

构造 PR/MR 的 title 和 body：

- **title**：`docs: AI 知识同步 - {draft-slug}`
- **body**：列出每条应用的知识点及目标文件、来源草稿文件名

**GitHub：**

```bash
gh pr create \
  --title "docs: AI 知识同步 - {draft-slug}" \
  --body "## 变更内容

{每条知识点：操作类型 + 目标文件}

## 来源
通过 \`/ai-docs-apply\` 从 AI 对话历史中提取并应用。
草稿来源：\`docs/07-ai-knowledge-inbox/{draft-filename}\`（已删除）" \
  --base {默认主分支}
```

**GitLab：**

```bash
glab mr create \
  --title "docs: AI 知识同步 - {draft-slug}" \
  --description "## 变更内容

{每条知识点：操作类型 + 目标文件}

## 来源
通过 \`/ai-docs-apply\` 从 AI 对话历史中提取并应用。
草稿来源：\`docs/07-ai-knowledge-inbox/{draft-filename}\`（已删除）" \
  --target-branch {默认主分支} \
  --remove-source-branch
```

### 5.5 异常处理

- 若 `gh`/`glab` 未安装：输出提示，跳过 PR/MR 创建，commit + push 仍正常执行
- 若 push 失败：报告错误，不强制操作

---

## Step 6：输出摘要

输出简洁摘要，包含：
- 成功应用的条目列表（操作类型 + 目标文件）
- 跳过的条目及原因
- Git commit hash
- PR/MR 链接（如创建成功）
