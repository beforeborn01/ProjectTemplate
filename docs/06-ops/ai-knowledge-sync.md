<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1447-ai-knowledge-workflow.md -->

# AI 知识同步运维规范

## 定时任务配置

| 项目 | 值 |
|-----|---|
| 执行频率 | 每天 08:47（本地时间）|
| 触发命令 | `/ai-docs-sync cron` |
| 调度方式 | Claude Code Session Cron（session-only，重启后需重建）|

> **注意**：Claude Code 的 Session Cron 在会话关闭后失效，且3天自动过期。如需持久化，应配置系统级 cron：
> ```bash
> # crontab -e
> 47 8 * * * cd /path/to/project && claude -p "/ai-docs-sync cron"
> ```

## Git 分支策略

定时任务产生的草稿文件提交到独立分支，避免污染主开发分支：

- **草稿分支**：`ai-knowledge-inbox`
- **合并时机**：人工审核并通过 `/ai-docs-apply` 应用后，由开发者手动合并
- **提交信息格式**：`ai: auto knowledge draft YYYY-MM-DD`

## 会话历史读取

Claude Code 将每个会话存储为 JSONL 文件，路径规则：

```
~/.claude/projects/<project-key>/<session-id>.jsonl
```

其中 `project-key` 是项目绝对路径，将 `/` 替换为 `-`（**保留**开头的 `-`，不做 lstrip）。

例如：`/Users/bc/code/common/ProjectTemplate` → `-Users-bc-code-common-ProjectTemplate`（目录名以 `-` 开头）

> **背景**：曾出现 `lstrip('-')` 导致匹配不到正确目录的 bug，根因是 Python 的 `lstrip` 会移除所有开头的 `-` 字符，而实际目录名 `-Users-...` 需要保留这个前导 `-`。

定时任务模式会读取最近1天内的所有会话文件，提取 `user` 和 `assistant` 角色的消息内容进行分析。

## Skill 文件管理

`.claude/skills/` 目录下的 skill 文件**必须纳入 git 追踪**，否则在切换分支时会丢失（未追踪文件在某些 git 操作下会被清理）。

```bash
git add .claude/skills/
git commit -m "chore: track skill files in git"
```

Skill 文件应随项目代码一起版本管理，和 CLAUDE.md 同等对待。

## 注意事项

### Claude Code 会话路径 key 格式

```python
# ✅ 正确：保留开头的 -
project_key = os.getcwd().replace('/', '-')

# ❌ 错误：lstrip 会去掉开头的 -，导致找不到会话目录
project_key = os.getcwd().replace('/', '-').lstrip('-')
```
