<!-- 创建日期：2026-03-11 | 来源草稿：2026-03-11-1506-feishu-maven-nacos-k8s.md -->

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
