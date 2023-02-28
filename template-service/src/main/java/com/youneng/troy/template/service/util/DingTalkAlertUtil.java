package com.youneng.troy.template.service.util;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.time.Instant;

/**
 * @author sunjianzhi
 */
@RefreshScope
@Configuration
@Slf4j
public class DingTalkAlertUtil {

    @Value("${spring.cloud.config.profile}")
    private String ENV;
    @Value("${basealert.dingtalk.enable:true}")
    private boolean dingTalkEnable;

    public void alert(Exception e, String user, String customMessage) {
        if (!dingTalkEnable) {
            return;
        }
        try {
            String content = "【环境】 : 【 " + ENV + " 】" +
                    "IP : " + InetAddress.getLocalHost().getHostAddress() +
                    "traceId : " + TraceContext.traceId() +
                    "user : " + user +
                    "customMessage : " + customMessage;
            sendMsg(content);
        } catch (Exception exception) {
            log.error("钉钉报警异常", e);
        }
    }

    private String sendMsg(String content) {
        try {
            //群机器人复制到的秘钥secret
            //String secret = "SECc986f2d199370a58870e17d86f976e12f6dc31bb6e3d71e44efas0a538bb5278";
            String secret = "你的秘钥secret";
            //获取系统时间戳
            long timestamp = Instant.now().toEpochMilli();
            //拼接
            String stringToSign = timestamp + "\n" + secret;

            Digester md5 = new Digester(DigestAlgorithm.MD5);
            String sign = md5.digestHex(stringToSign);

            //钉钉机器人地址（配置机器人的webhook）
            String dingUrl = "https://oapi.dingtalk.com/robot/send?access_token=a99413dtd5524b651f289357ab84196fb2bbf13d2c0c925c1f1d2d1df89b74b3&timestamp=" + timestamp + "&sign=" + sign;
            return HttpUtil.post(dingUrl, content);
        } catch (Exception e) {
            log.error("钉钉推送消息出现异常");
            return null;
        }
    }
}
