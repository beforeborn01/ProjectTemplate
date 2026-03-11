# ProjectTemplate

基于 Spring Boot 的微服务项目脚手架，使用 Maven Archetype 机制生成新项目。

## 技术栈

| 组件 | 版本 | 说明 |
|---|---|---|
| **JDK** | 21 | LTS 长期支持版本 |
| **Spring Boot** | 3.5.11 | 核心框架 |
| **Spring Cloud** | 2025.0.0 | 微服务组件（OpenFeign、LoadBalancer） |
| **Spring Cloud Alibaba** | 2025.0.0.0 | Nacos 注册/配置中心 |
| **MyBatis** | 3.0.4 | ORM 持久层 |
| **Druid** | 1.2.27 | 数据库连接池 |
| **JetCache** | 2.7.8 | 两级缓存（本地 + Redis） |
| **Testcontainers** | 2.0.3 | 集成测试容器化 |
| **SkyWalking** | 9.6.0 | 链路追踪 |
| **Spring AI** | 1.1.2 | AI 能力集成（OpenAI） |

## 中间件服务端版本

| 中间件 | 推荐版本 | 备注 |
|---|---|---|
| **MySQL** | 8.0.x / 8.4 LTS | Connector/J 9.6.0 兼容 |
| **Redis** | 7.2+ | JetCache / Spring Data Redis 兼容 |
| **Nacos** | 2.4.x | Spring Cloud Alibaba 2025 兼容 |

## 项目结构

```
ProjectTemplate/
├── template-common/    # 公共工具类、常量、基础实体
├── template-dao/       # 数据访问层（MyBatis Mapper、PO）
├── template-service/   # 业务逻辑层
├── template-biz/       # 业务编排层
├── template-api/       # 对外 API 接口定义（DTO、Feign Client）
├── template-client/    # 服务调用客户端
├── template-web/       # Web 启动模块（Controller、配置、测试）
├── k8s/                # Kubernetes 部署清单
├── Dockerfile          # 多阶段构建镜像
├── docker-compose.yml  # 本地开发环境
└── archetype.properties # Archetype 变量配置
```

## 快速开始

### 1. 发布 Archetype

```bash
# 修改 archetype.properties 中的配置信息
# 执行打包发布脚本
./build-mac.sh
```

### 2. 创建新项目

**方式一：命令行**
```bash
./createProject.sh
```

**方式二：IntelliJ IDEA**
- File → New → Project → Maven Archetype
- 选择已安装的 archetype，填写 GroupId / ArtifactId

### 3. 本地开发

```bash
# 启动依赖中间件（MySQL、Redis、Nacos）
docker-compose up -d mysql redis nacos

# 编译 & 测试
mvn clean install

# 启动应用
mvn spring-boot:run -pl template-web
```

## K8s 部署

项目已包含完整的 Kubernetes 部署支持：

- **Dockerfile** — 多阶段构建、Alpine 轻量镜像、非 root 用户、容器感知 JVM 参数
- **k8s/deployment.yaml** — 滚动更新、健康探针、资源限制、优雅停机
- **k8s/service.yaml** — ClusterIP 服务
- **k8s/configmap.yaml** — 外部化配置
- **k8s/kustomization.yaml** — Kustomize 资源管理

```bash
# 构建镜像
docker build -t your-registry/template:latest .

# 部署到 K8s
kubectl apply -k k8s/

# 或使用 Kustomize 修改镜像版本
cd k8s && kustomize edit set image your-registry/template:v1.0.0
kubectl apply -k .
```

### 健康检查端点

| 端点 | 用途 |
|---|---|
| `/actuator/health/liveness` | K8s 存活探针 |
| `/actuator/health/readiness` | K8s 就绪探针 |
| `/actuator/prometheus` | Prometheus 指标采集 |

## archetype.properties 变量替换

详见 `archetype.properties` 文件中的配置注释。
