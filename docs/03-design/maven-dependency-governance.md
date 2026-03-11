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
