# AI 知识草稿

- **来源**：manual / 2026-03-11
- **状态**：待确认 (Draft)

---

## 条目 1：ai-docs-apply 新增 PR/MR 自动创建功能

- **操作类型**：MODIFY
- **目标文件**：`docs/02-architecture/ai-knowledge-workflow.md`
- **目标章节**：`## 相关工具`（追加到该章节末尾，或新增 `## ai-docs-apply 工作流` 章节）
- **标签**：#架构 #AI工作流 #GitHub #GitLab

### 追加内容

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

---

## 条目 2：Skill 文件必须纳入 git 追踪

- **操作类型**：MODIFY
- **目标文件**：`docs/06-ops/ai-knowledge-sync.md`
- **目标章节**：`## 注意事项`（不存在则新增）
- **标签**：#运维 #Git #Skill

### 追加内容

## 注意事项

### Skill 文件必须提交到 git

`.claude/skills/` 目录下的 skill 文件是**未追踪文件（untracked）**，在 git 分支切换时可能丢失。

**必须执行：**
```bash
git add .claude/skills/
git commit -m "chore: add/update claude skills"
```

skill 文件应与项目代码同步维护在正确的分支上。

### Claude Code 会话路径 key 格式

读取会话历史时，project key 的正确生成方式：

```python
# ✅ 正确：保留开头的 -
project_key = os.getcwd().replace('/', '-')

# ❌ 错误：lstrip 会去掉开头的 -，导致找不到会话目录
project_key = os.getcwd().replace('/', '-').lstrip('-')
```

---

## 确认意见

- [ ] 已审核
- [ ] 需修正（请在下方补充意见）
- [ ] 已通过 /ai-docs-apply 应用到正式文档

> 修正意见：
