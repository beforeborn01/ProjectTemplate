<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1506-feishu-maven-nacos-k8s.md -->

# 配置管理策略：Nacos + K8s 混合模式

## 决策

采用 **K8s 管基础设施 + Nacos 管业务配置** 的混合模式。

## 职责划分

| 配置类型 | 管理方式 | 示例 |
|---------|---------|------|
| 基础设施配置 | K8s ConfigMap / Secret | 内存限制、副本数、JVM 参数、数据库密码 |
| 业务配置 | Nacos | 功能开关、限流阈值、业务规则参数 |
| Nacos 连接地址本身 | K8s ConfigMap 注入 | `NACOS_SERVER_ADDR` 环境变量 |

## 关键设计：避免"鸡生蛋"问题

Nacos 的 `server-addr` 不能放在 Nacos 自身管理，否则服务无法启动。解决方案：

- **本地开发**：`bootstrap.yml` 写死 `http://127.0.0.1:8848`
- **生产/测试环境**：通过 K8s ConfigMap 注入 `NACOS_SERVER_ADDR` 环境变量，`bootstrap.yml` 读取 `${NACOS_SERVER_ADDR:http://127.0.0.1:8848}`

这样本地无需改配置即可直接启动，部署时由 K8s 覆盖。
