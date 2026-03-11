# AI 知识草稿

- **来源**：cron / 2026-03-11
- **状态**：待确认 (Draft)

---

## 条目 1：服务发现从 Consul 迁移到 Nacos

- **操作类型**：NEW_FILE
- **目标路径**：`docs/02-architecture/service-discovery-nacos.md`
- **标签**：#架构 #服务发现 #Nacos #Consul

### 内容草稿

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

---

## 条目 2：Nacos 服务发现配置规范

- **操作类型**：NEW_FILE
- **目标路径**：`docs/06-ops/nacos-config.md`
- **标签**：#运维 #Nacos #配置 #服务发现

### 内容草稿

# Nacos 配置规范

## 配置中心（nacos.config）

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: http://127.0.0.1:8848   # 本地开发用本机地址，生产环境通过 K8s 环境变量注入
        file-extension: yaml
        namespace: troy                        # 命名空间，按环境隔离
        ext-config[0]:
          data-id: common.yaml
          group: ${spring.cloud.config.profile}
          refresh: true
        ext-config[1]:
          data-id: ${spring.application.name}-${spring.cloud.config.profile}.yaml
          group: ${spring.cloud.config.profile}
          refresh: true
```

## 服务发现（nacos.discovery）

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: http://127.0.0.1:8848   # 同上，生产环境注入
        namespace: troy
        service: ${spring.application.name}
        group: ${spring.cloud.config.profile}
        cluster-name: DEFAULT
        metadata:
          version: 1.0
        heart-beat-interval: 5000            # 心跳间隔（ms）
        heart-beat-timeout: 15000            # 心跳超时（ms）
        ip-delete-timeout: 30000             # IP 摘除超时（ms）
```

## 注意事项

- `server-addr` 本地开发写 `127.0.0.1:8848`，**不要提交生产地址**到代码仓库
- 生产/测试环境的 `server-addr` 通过 K8s ConfigMap 或环境变量注入，与 Nacos 本身的配置管理解耦（避免鸡生蛋问题）
- `namespace` 统一使用 `troy`，不同环境通过 `group`（对应 profile）区分

---

## 确认意见

- [ ] 已审核
- [ ] 需修正（请在下方补充意见）
- [ ] 已通过 /ai-docs-apply 应用到正式文档

> 修正意见：
