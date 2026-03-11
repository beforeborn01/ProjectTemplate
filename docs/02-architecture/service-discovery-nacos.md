<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1459-nacos-service-discovery.md -->

# 服务发现：从 Consul 迁移到 Nacos

## 决策背景

项目原使用 Consul 作为服务发现组件，Nacos 仅用于配置中心。为统一使用 Alibaba Cloud 生态、减少基础设施依赖，决定将服务发现也迁移至 Nacos，由 Nacos 同时承担配置中心与服务发现两个职责。

## 变更范围

| 文件 | 变更内容 |
|------|---------|
| `template-web/pom.xml` | 替换 `spring-cloud-starter-consul-discovery` → `spring-cloud-starter-alibaba-nacos-discovery` |
| `pom.xml`（父工程） | 在 dependencyManagement 中新增 `spring-cloud-starter-alibaba-nacos-discovery` 托管版本 |
| `template-web/src/main/resources/bootstrap.yml` | 移除 `spring.cloud.consul` 配置块，新增 `spring.cloud.nacos.discovery` 配置块 |

## 架构影响

- Nacos 现在同时提供：**配置管理**（`nacos.config`）+ **服务注册与发现**（`nacos.discovery`）
- 可移除 Consul 服务器依赖，降低运维复杂度
- 服务间调用的负载均衡、健康检查均由 Nacos 接管
