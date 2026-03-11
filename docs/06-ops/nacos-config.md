<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1459-nacos-service-discovery.md -->

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
