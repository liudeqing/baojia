package com.baojia.user_manager.service;

import reactor.core.publisher.Flux;

public interface AIChatService {

    /**
     * 基于 RAG 的回答：检索知识库 + 提示词增强
     */
    String askWithRag(String userQuestion);

    /**
     * 普通对话（不依赖 RAG，仅靠模型自身）
     */
    String chat(String message);

    /**
     * 流式回答：基于 RAG 检索后，逐字返回 AI 生成的内容
     * @param userQuestion 用户问题
     * @return Flux<String> 每个元素是生成的一小段文本（通常是一个 token 或几个字符）
     */
    Flux<String> askStreamFromRag(String userQuestion);
}
