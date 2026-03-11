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

其中 `project-key` 是项目绝对路径，将 `/` 替换为 `-` 并去掉开头的 `-`。

例如：`/Users/bc/code/common/ProjectTemplate` → `-Users-bc-code-common-ProjectTemplate`

定时任务模式会读取最近1天内的所有会话文件，提取 `user` 和 `assistant` 角色的消息内容进行分析。
