package com.bc.template.service.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI 聊天服务示例
 * <p>
 * 演示使用 Spring AI ChatClient 调用 OpenAI，
 * 包括普通调用、流式调用和 Tool Calling。
 * </p>
 *
 * <p>
 * 使用前请确保配置了 spring.ai.openai.api-key 属性。
 * 未配置时此 Bean 不会被创建（通过 ConditionalOnProperty 控制）。
 * </p>
 */
@Service
@ConditionalOnProperty(name = "spring.ai.openai.api-key")
public class DemoChatService {

    private final ChatClient chatClient;

    public DemoChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 普通聊天调用（同步）
     *
     * @param userMessage 用户输入
     * @return AI 回复文本
     */
    public String chat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * 带系统提示词的聊天
     *
     * @param systemPrompt 系统提示词（角色设定）
     * @param userMessage  用户输入
     * @return AI 回复文本
     */
    public String chatWithSystemPrompt(String systemPrompt, String userMessage) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }

    /**
     * 流式聊天调用（SSE 场景）
     *
     * @param userMessage 用户输入
     * @return 流式响应
     */
    public Flux<String> chatStream(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .stream()
                .content();
    }

    /**
     * 带 Tool Calling 的聊天（原 Function Calling，在 Spring AI 1.0+ 改名为 Tool Calling）
     * <p>
     * 使用方式：在 Spring 容器中注册一个函数 Bean，
     * 然后通过 toolNames() 方法引用 Bean 名，AI 会在需要时自动调用。
     * </p>
     *
     * <pre>{@code
     * // 1. 在配置类中定义工具 Bean
     * &#64;Bean
     * &#64;Description("根据城市名查询当前天气")
     * public Function<WeatherRequest, WeatherResponse> getWeather() {
     *     return request -> weatherService.query(request.city());
     * }
     *
     * // 2. 调用此方法，AI 遇到天气问题会自动调用 getWeather 工具
     * chatService.chatWithTools("北京今天天气怎么样？", "getWeather");
     * }</pre>
     *
     * @param userMessage 用户输入
     * @param toolNames   可用的工具 Bean 名列表
     * @return AI 回复文本（可能包含工具调用结果）
     */
    public String chatWithTools(String userMessage, String... toolNames) {
        return chatClient.prompt()
                .user(userMessage)
                .toolNames(toolNames)
                .call()
                .content();
    }
}
