package com.baojia.ai_manager.controller;

import com.baojia.user_manager.service.AIChatService;
import com.baojia.user_manager.service.KnowledgeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@SuppressWarnings("all")
public class ChatAiController {

    private final KnowledgeService knowledgeService;
    private final AIChatService aiChatService;

    public ChatAiController(KnowledgeService knowledgeService, AIChatService aiChatService) {
        this.knowledgeService = knowledgeService;
        this.aiChatService = aiChatService;
    }

    // 这里是手动上传知识（支持纯文本）
    @PostMapping("/knowledge")
    public String uploadKnowledge(@RequestBody String knowledgeText) {
        if (knowledgeText == null || knowledgeText.isBlank()) {
            return "请提供非空的文本内容";
        }
        knowledgeService.loadKnowledge(knowledgeText);
        return "知识已加载！现在你可以问 AI 关于这些内容的问题。";
    }

    // 让 AI 介绍自己（基于已加载的知识）
    @GetMapping("/introduce")
    public String introduce() {
        return aiChatService.askWithRag("请介绍一下你自己");
    }

    // 自由对话（基于 RAG）
    @GetMapping("/chat")
    public String chat(@RequestParam(name = "msg") String msg) {
        return aiChatService.askWithRag(msg);
    }

    // 流式输出（基于 RAG）
    @GetMapping("/chatstream")
    public Flux<String> chatStream(@RequestParam(name = "msg") String msg) {
        return aiChatService.askStreamFromRag(msg);
    }

    // 普通对话（不使用 RAG，仅模型原生能力）
    @GetMapping("/raw")
    public String rawChat(@RequestParam(name = "msg") String msg) {
        return aiChatService.chat(msg);
    }

}
