# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 基本规则

- **永远使用中文回答用户问题**

## 项目概述

本项目是一个面向 Spring Boot 微服务的 **Maven Archetype 模板**。项目本身展示了模板的约定规范，并通过 `./build-mac.sh` 生成新项目。

## 构建与运行命令

```bash
# 构建所有模块
mvn clean install

# 运行应用（入口模块为 template-web）
mvn spring-boot:run -pl template-web

# 运行所有测试
mvn test

# 运行指定测试类
mvn test -pl template-web -Dtest=DemoControllerTest

# 运行指定测试方法
mvn test -pl template-web -Dtest=DemoControllerTest#should_get_user_by_id

# 启动本地中间件（MySQL、Redis、Nacos）
docker-compose up -d mysql redis nacos

# 构建 Maven Archetype（生成 archetype 制品）
./build-mac.sh
```

## 模块架构

7 模块分层架构（整洁架构）：

```
template-common    # 工具类、常量、基础实体 — 无内部依赖
template-api       # DTO、API 接口、Feign 客户端定义
template-dao       # MyBatis Mapper、PO 实体 → 依赖 common
template-service   # 业务逻辑 → 依赖 dao、common
template-client    # Feign 客户端实现 → 依赖 api、common
template-biz       # 业务编排 → 依赖 service、client、common
template-web       # Web 入口：Controller、Filter、Aspect → 聚合所有模块
```

**依赖规则：下层模块不得依赖上层模块。** 新业务逻辑放在 `template-service`；跨服务编排放在 `template-biz`；REST 接口放在 `template-web`。

## 核心技术栈

| 组件 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.5.11 |
| Spring Cloud | 2025.0.0 |
| Spring Cloud Alibaba (Nacos) | 2025.0.0.0 |
| Spring AI (OpenAI) | 1.1.2 |
| MyBatis | 3.0.4 |
| Druid | 1.2.27 |
| JetCache | 2.7.8（本地 + Redis） |
| Testcontainers | 2.0.3 |

## 应用入口

`com.bc.template.web.ApplicationStarter` — 注解包含 `@EnableDiscoveryClient`（Nacos）、`@EnableFeignClients("com.bc")`、`@EnableMethodCache("com.bc.template.web")`（JetCache）。

服务端口：**9915**。配置文件：`template-web/src/main/resources/bootstrap.yml`。

## 测试方式

测试代码位于 `template-web/src/test/`。集成测试通过 **Testcontainers** 启动真实的 MySQL 和 Redis 容器，不 mock 数据库。

- `MysqlContainerBase` / `RedisContainerBase` — JUnit 5 扩展，自动启动容器。
- 测试 profile：`spring.profiles.active=test`；测试环境中 Nacos 服务发现/配置中心已禁用。
- OpenAI API 调用通过 **WireMock** mock（端口 18089）。
- 测试数据库由 `template-web/src/test/resources/table.sql` 初始化。

## 基础设施与部署

- **本地开发：** `docker-compose.yml`（MySQL 8.0 端口 3306，Redis 端口 88881，Nacos 端口 8848）
- **Kubernetes：** `k8s/` 目录使用 Kustomize — 2 个副本，滚动更新，60 秒优雅终止
- **Docker：** 多阶段构建（Alpine JRE 21），非 root 用户，时区 Asia/Shanghai
- **OpenAI API Key：** 通过环境变量 `OPENAI_API_KEY` 注入
