package com.bc.template.service.util;

import cn.hutool.http.HttpRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * 飞书群机器人报警工具类
 * <p>
 * 使用前需配置飞书群机器人 Webhook，并在环境变量中注入：
 * - FEISHU_WEBHOOK_URL：机器人 Webhook 地址
 * - FEISHU_SECRET：签名校验密钥（机器人开启签名校验时必填）
 * </p>
 */
@Component
@Slf4j
public class FeishuAlertUtil {

    @Value("${spring.cloud.config.profile:dev}")
    private String ENV;
    @Value("${basealert.feishu.enable:true}")
    private boolean feishuEnable;
    @Value("${basealert.feishu.webhook:}")
    private String webhookUrl;
    @Value("${basealert.feishu.secret:}")
    private String secret;

    public void alert(Exception e, String user, String customMessage) {
        if (!feishuEnable) {
            return;
        }
        try {
            String content = "【环境】: 【 " + ENV + " 】\n" +
                    "IP: " + InetAddress.getLocalHost().getHostAddress() + "\n" +
                    "traceId: " + Optional.ofNullable(MDC.get("tid")).orElse("N/A") + "\n" +
                    "user: " + user + "\n" +
                    "message: " + customMessage;
            sendMsg(content);
        } catch (Exception exception) {
            log.error("飞书报警异常", exception);
        }
    }

    private void sendMsg(String content) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("飞书 Webhook 未配置，跳过发送");
            return;
        }
        try {
            long timestamp = Instant.now().getEpochSecond();
            String body = buildRequestBody(timestamp, content);
            String response = HttpRequest.post(webhookUrl)
                    .contentType("application/json")
                    .body(body)
                    .execute()
                    .body();
            log.info("飞书消息发送结果: {}", response);
        } catch (Exception e) {
            log.error("飞书推送消息出现异常", e);
        }
    }

    private String buildRequestBody(long timestamp, String content) throws Exception {
        String sign = "";
        if (secret != null && !secret.isBlank()) {
            // 飞书官方签名算法：key = (timestamp + "\n" + secret)，data = 空
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            sign = Base64.getEncoder().encodeToString(mac.doFinal(new byte[]{}));
        }

        // 飞书文本消息格式
        return "{" +
                "\"timestamp\":\"" + timestamp + "\"," +
                "\"sign\":\"" + sign + "\"," +
                "\"msg_type\":\"text\"," +
                "\"content\":{\"text\":\"" + escapeJson(content) + "\"}" +
                "}";
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
