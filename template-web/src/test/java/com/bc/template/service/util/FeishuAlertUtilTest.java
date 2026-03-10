package com.bc.template.service.util;

import cn.hutool.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 飞书群机器人报警集成测试
 * <p>
 * 执行此测试会向真实飞书群发送消息，请在飞书群中确认消息是否送达。
 * Webhook 和 Secret 读取环境变量，回退到 application-test.yml 中的默认值。
 * </p>
 */
class FeishuAlertUtilTest {

    private static final String WEBHOOK_URL = System.getenv().getOrDefault(
            "FEISHU_WEBHOOK_URL",
            "https://open.feishu.cn/open-apis/bot/v2/hook/64e6d761-3850-4113-a131-140b6f25da51");

    private static final String SECRET = System.getenv().getOrDefault(
            "FEISHU_SECRET",
            "oqpOdfg4PWivV0c4Li5pUe");

    private FeishuAlertUtil feishuAlertUtil;

    @BeforeEach
    void setUp() throws Exception {
        feishuAlertUtil = new FeishuAlertUtil();
        setField("ENV", "test");
        setField("feishuEnable", true);
        setField("webhookUrl", WEBHOOK_URL);
        setField("secret", SECRET);
    }

    /**
     * 直接调用飞书 Webhook，断言 code=0，验证签名算法和连通性。
     * 这是最可靠的集成验证手段——响应不再被 alert() 的 catch 块吞掉。
     */
    @Test
    @DisplayName("直接 HTTP 调用 - 验证签名正确且飞书返回 code:0")
    void should_receive_success_response_from_feishu() throws Exception {
        long timestamp = Instant.now().getEpochSecond();
        String sign = genSign(SECRET, timestamp);

        String body = "{" +
                "\"timestamp\":\"" + timestamp + "\"," +
                "\"sign\":\"" + sign + "\"," +
                "\"msg_type\":\"text\"," +
                "\"content\":{\"text\":\"【集成测试】签名验证 ✅ timestamp=" + timestamp + "\"}" +
                "}";

        String response = HttpRequest.post(WEBHOOK_URL)
                .contentType("application/json")
                .body(body)
                .execute()
                .body();

        System.out.println("飞书响应: " + response);
        assertTrue(response.contains("\"code\":0"), "飞书返回非 0，响应: " + response);
    }

    @Test
    @DisplayName("发送普通告警消息 - 验证 alert() 不抛异常")
    void should_send_alert_without_exception() {
        assertDoesNotThrow(() -> feishuAlertUtil.alert(null, "test-user", "【集成测试】飞书消息发送验证 ✅"));
    }

    @Test
    @DisplayName("发送异常告警消息 - 验证携带异常信息时不抛异常")
    void should_send_alert_with_exception() {
        Exception testException = new RuntimeException("模拟系统异常：数据库连接超时");

        assertDoesNotThrow(() -> feishuAlertUtil.alert(testException, "test-user", "【集成测试】异常告警验证，请忽略"));
    }

    /**
     * 飞书官方签名算法：key = (timestamp + "\n" + secret)，data = 空
     */
    private String genSign(String secret, long timestamp) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return Base64.getEncoder().encodeToString(mac.doFinal(new byte[] {}));
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = FeishuAlertUtil.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(feishuAlertUtil, value);
    }
}
