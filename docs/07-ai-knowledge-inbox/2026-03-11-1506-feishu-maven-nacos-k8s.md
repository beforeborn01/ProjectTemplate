# AI 知识草稿

- **来源**：cron / 2026-03-11
- **状态**：待确认 (Draft)

---

## 条目 1：飞书告警集成设计规范

- **操作类型**：NEW_FILE
- **目标路径**：`docs/03-design/feishu-alert-integration.md`
- **标签**：#设计 #飞书 #告警 #测试

### 内容草稿

# 飞书群机器人告警集成设计

## 背景

项目原使用钉钉 SDK 发送告警，存在 token/secret 硬编码在代码中的安全问题（Code Review Critical 级别）。迁移至飞书群机器人后同步消除该问题。

## 实现关键决策

### 1. Bean 注解选择：`@Component` 而非 `@Configuration`

```java
@Component  // ✅ 正确
// @Configuration  ❌ 会触发 CGLIB 代理，干扰加密方法调用
public class FeishuAlertUtil { ... }
```

### 2. 签名算法：使用 `javax.crypto.Mac`

飞书官方签名要求 HMAC-SHA256，使用标准库 `javax.crypto.Mac`，与官方示例完全一致：

```java
Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
byte[] signData = mac.doFinal((timestamp + "\n" + secret).getBytes(StandardCharsets.UTF_8));
String sign = Base64.getEncoder().encodeToString(signData);
```

### 3. TraceId 获取：使用 `MDC` 而非 SkyWalking 切面

```java
String traceId = MDC.get("tid");  // ✅ 不依赖 SkyWalking 切面
// 不使用 TraceContext.traceId()  ❌ 引入 SkyWalking 强依赖
```

### 4. 配置外部化：Webhook 和 Secret 通过环境变量注入

```yaml
# bootstrap.yml / application.yml
feishu:
  webhook-url: ${FEISHU_WEBHOOK_URL}
  secret: ${FEISHU_SECRET}
```

K8s ConfigMap 中需补充 `FEISHU_WEBHOOK_URL` 和 `FEISHU_SECRET` 条目。

## 测试策略

飞书告警工具类的集成测试**不依赖 Spring 上下文、MySQL、Redis**：

```java
// ✅ 纯 Java 对象测试，执行约 0.4 秒
class FeishuAlertUtilTest {
    // should_receive_success_response_from_feishu：断言返回 code:0，签名失效立即失败
    // alert() 边界测试：验证不抛异常的行为边界
}
```

测试用的 webhook/secret 在 `application-test.yml` 中配置（使用测试专用机器人），可提交到代码仓库。

---

## 条目 2：Maven 依赖治理规范

- **操作类型**：NEW_FILE
- **目标路径**：`docs/03-design/maven-dependency-rules.md`
- **标签**：#设计 #Maven #依赖管理

### 内容草稿

# Maven 依赖治理规范

经 pom 代码评审后沉淀以下规则，适用于本项目所有模块。

## 版本管理

**规则：子模块版本统一使用 `${revision}`，禁止定义冗余版本变量**

```xml
<!-- ❌ 禁止：定义冗余版本变量 -->
<api-version>1.0-SNAPSHOT</api-version>
<web-version>1.0-SNAPSHOT</web-version>

<!-- ✅ 正确：子模块直接省略 <version> 或统一用 ${revision} -->
<revision>1.0-SNAPSHOT</revision>
```

## 模块职责边界

**规则：`template-api` 仅包含 DTO/接口定义，禁止引入 `spring-boot-starter-web`**

`template-api` 是被所有模块依赖的基础层，引入 `starter-web` 会传递 Tomcat、Jackson 等重量级依赖至全部下游模块。

```xml
<!-- template-api/pom.xml ❌ 禁止 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## 测试依赖 Scope

**规则：测试框架依赖必须显式声明 `<scope>test</scope>`**

```xml
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <scope>test</scope>  <!-- 必须声明，防止污染生产 classpath -->
</dependency>
```

## 工具库去重

**规则：`hutool-all` 等通用工具库在父 pom `dependencyManagement` 中统一管理，子模块不重复引入**

---

## 条目 3：Nacos + K8s 混合配置策略

- **操作类型**：MODIFY
- **目标文件**：`docs/02-architecture/ai-knowledge-workflow.md`
- **目标章节**：追加到文件末尾作为新章节（该决策尚无独立文档）
- **标签**：#架构 #Nacos #K8s #配置管理

> **建议**：实际应新建 `docs/02-architecture/config-management-strategy.md`，操作类型改为 NEW_FILE。

- **操作类型**：NEW_FILE
- **目标路径**：`docs/02-architecture/config-management-strategy.md`
- **标签**：#架构 #Nacos #K8s #配置管理

### 内容草稿

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

---

## 确认意见

- [ ] 已审核
- [ ] 需修正（请在下方补充意见）
- [ ] 已通过 /ai-docs-apply 应用到正式文档

> 修正意见：
