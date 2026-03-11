package com.bc.template.service.ai;

import com.bc.template.MysqlContainerBase;
import com.bc.template.RedisContainerBase;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Spring AI 集成测试 — 使用 WireMock 模拟 OpenAI API
 * <p>
 * 验证完整链路：Spring AI AutoConfig → ChatClient Bean → HTTP 请求 → 响应解析
 * 无需真实 API Key，通过 WireMock 拦截 HTTP 请求并返回预设响应。
 * </p>
 */
@SpringBootTest(classes = com.bc.template.web.ApplicationStarter.class, properties = {
        "spring.profiles.active=test",
        "spring.ai.openai.base-url=http://localhost:18089"
})
@ExtendWith({ MysqlContainerBase.class, RedisContainerBase.class })
class DemoChatServiceTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private DemoChatService demoChatService;

    /**
     * OpenAI Chat Completions API 的标准响应格式
     */
    private static final String CHAT_COMPLETION_RESPONSE = "{" +
            "  \"id\": \"chatcmpl-test-123\"," +
            "  \"object\": \"chat.completion\"," +
            "  \"created\": 1700000000," +
            "  \"model\": \"gpt-4o\"," +
            "  \"choices\": [" +
            "    {" +
            "      \"index\": 0," +
            "      \"message\": {" +
            "        \"role\": \"assistant\"," +
            "        \"content\": \"你好！我是 AI 助手，有什么可以帮你的？\"" +
            "      }," +
            "      \"finish_reason\": \"stop\"" +
            "    }" +
            "  ]," +
            "  \"usage\": {" +
            "    \"prompt_tokens\": 10," +
            "    \"completion_tokens\": 20," +
            "    \"total_tokens\": 30" +
            "  }" +
            "}";

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(18089));
        wireMockServer.start();
        WireMock.configureFor("localhost", 18089);
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setupStubs() {
        wireMockServer.resetAll();

        // Mock OpenAI Chat Completions API: POST /v1/chat/completions
        stubFor(post(urlPathEqualTo("/v1/chat/completions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(CHAT_COMPLETION_RESPONSE)));
    }

    @Test
    @DisplayName("普通聊天 - 验证 ChatClient 能正确调用 OpenAI 并解析响应")
    void should_chat_and_get_response() {
        String response = demoChatService.chat("你好");

        assertNotNull(response, "AI 回复不应为 null");
        assertTrue(response.contains("AI 助手"), "回复应包含预设内容");

        // 验证 WireMock 确实收到了请求
        verify(postRequestedFor(urlPathEqualTo("/v1/chat/completions"))
                .withHeader("Authorization", matching("Bearer sk-test-fake-key-for-unit-tests")));
    }

    @Test
    @DisplayName("带系统提示词聊天 - 验证 system prompt 被正确传入请求")
    void should_chat_with_system_prompt() {
        String response = demoChatService.chatWithSystemPrompt(
                "你是一个 Java 专家",
                "Spring AI 是什么？");

        assertNotNull(response);

        // 验证请求体包含 system 消息
        verify(postRequestedFor(urlPathEqualTo("/v1/chat/completions"))
                .withRequestBody(containing("\"role\":\"system\"")));
    }

    @Test
    @DisplayName("DemoChatService Bean 应成功注入 - 验证 Spring AI 自动配置正常工作")
    void should_inject_demo_chat_service() {
        assertNotNull(demoChatService, "DemoChatService Bean 应该被正常创建和注入");
    }
}
