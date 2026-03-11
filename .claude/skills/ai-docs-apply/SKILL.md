---
name: ai-docs-apply
description: 将 docs/07-ai-knowledge-inbox/ 下已确认的草稿文件应用到正式文档。读取草稿中的操作指令（新建文件或修改已有文件），执行实际变更，删除草稿，提交到当前分支。
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

## Step 5：提交变更

将所有变更（新建/修改的正式文档 + 删除的草稿文件）一并提交到当前分支：

```bash
git add -A
git commit -m "docs: apply ai knowledge draft {draft-filename-without-ext}"
```

---

## Step 6：输出摘要

输出简洁摘要，包含：
- 成功应用的条目列表（操作类型 + 目标文件）
- 跳过的条目及原因
- Git commit hash
