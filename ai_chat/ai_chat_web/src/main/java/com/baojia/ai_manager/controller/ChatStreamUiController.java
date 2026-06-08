package com.baojia.ai_manager.controller;

import com.baojia.user_manager.service.AIChatService;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import reactor.core.publisher.Flux;

/**
 * 聊天页面前端专用控制器（与 {@link ChatAiController} 独立，不修改原有接口）。
 * <p>
 * 职责：
 * <ul>
 *   <li>将 /chat-ui 重定向到静态 React 页面</li>
 *   <li>提供 POST 流式接口，避免 GET 长文本 URL 编码与长度限制问题</li>
 * </ul>
 */
@Controller
@RequestMapping("/chat-ui")
public class ChatStreamUiController {

    private final AIChatService aiChatService;

    public ChatStreamUiController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * 浏览器访问 http://localhost:8085/chat-ui 进入聊天窗口。
     */
    @GetMapping
    public RedirectView chatPage() {
        return new RedirectView("/chat/index.html");
    }

    /**
     * RAG 流式对话（SSE），供 React 前端 fetch 逐段读取。
     * 对应服务层 {@link AIChatService#askStreamFromRag(String)}。
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> streamRag(@RequestBody ChatMessageRequest request) {
        String message = request != null ? request.getMessage() : null;
        if (message == null || message.isBlank()) {
            return Flux.just("请输入有效的问题内容。");
        }
        return aiChatService.askStreamFromRag(message.trim());
    }

    /**
     * 前端 POST 请求体：仅包含用户输入的一条消息。
     */
    @Data
    public static class ChatMessageRequest {
        /** 用户输入的聊天内容 */
        private String message;
    }
}
