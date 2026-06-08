package com.baojia.user_manager.service.impl;

import com.baojia.user_manager.service.AIChatService;
import com.baojia.user_manager.service.KnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AIChatServiceImpl implements AIChatService {

    private final ChatClient chatClient;

    private final KnowledgeService knowledgeService;

    public AIChatServiceImpl(ChatClient.Builder chatClientBuilder,
                         KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
        // 启用对话记忆，保留最近10条消息
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId("rag-user")
                        .build())
                .build();
    }

    /**
     * 基于 RAG 的回答：检索知识库 + 提示词增强
     */
    public String askWithRag(String userQuestion) {
        // 1. 检索相关片段
        List<Document> relevantDocs = knowledgeService.retrieveRelevantDocs(userQuestion, 3);
        String knowledgeContext = relevantDocs.stream()
                .map(doc -> {
                    log.info("------------------------");
                    log.info("来源:{} " , doc.getMetadata().get("source"));
                    log.info("内容: {}" , doc.getText().substring(0, Math.min(200, doc.getText().length())));
                    log.info("------------------------");
                    String source = doc.getMetadata().get("source").toString();
                    return "【来源：" + source + "】\n" + doc.getText();
                })
                .collect(Collectors.joining("\n\n"));

        // 如果没检索到任何内容，给一个默认提示
        if (knowledgeContext.isBlank()) {
            knowledgeContext = "（当前知识库为空，无法提供相关信息）";
        }

        // 2. 构造 prompt
        String promptTemplateStr = """
                你是一个基于知识库回答问题的助手。
                请严格根据下面提供的知识内容回答问题。
                如果知识内容中无法找到答案，请直接回答“根据已有资料无法回答该问题”，不要编造信息。
                
                知识内容：
                {knowledge}
                
                用户问题：{question}
                
                请用中文、简洁自然地回答：
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptTemplateStr);
        String prompt = promptTemplate.create(Map.of(
                "knowledge", knowledgeContext,
                "question", userQuestion
        )).getContents();

        // 3. 调用模型
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    /**
     * 普通对话（不依赖 RAG，仅靠模型自身）
     */
    public String chat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }


    /**
     * 流式回答：基于 RAG 检索后，逐字返回 AI 生成的内容
     * @param userQuestion 用户问题
     * @return Flux<String> 每个元素是生成的一小段文本（通常是一个 token 或几个字符）
     */
    @Override
    public Flux<String> askStreamFromRag(String userQuestion) {
        // 1. 检索相关文档片段（同步操作，可以保留）
        List<Document> relevantDocs = knowledgeService.retrieveRelevantDocs(userQuestion, 3);
        String knowledgeContext = relevantDocs.stream()
                .map(doc ->
                        "【来源：" + doc.getMetadata().get("source") + "】\n" + doc.getText()
                ).peek(s->{
                    log.info( "命中文档:{}" , s );
                })
                .collect(Collectors.joining("\n\n"));

        if (knowledgeContext.isBlank()) {
            // 无知识时直接返回提示
            return Flux.just("（知识库为空，无法提供相关信息）");
        }

        // 2. 构造 prompt（和之前一样）
        String promptTemplateStr = """
                你是一个严格基于知识库回答问题的助手。
                请只根据下面【知识内容】回答问题。如果知识中没有答案，请直接说“根据已有资料无法回答”。
                
                【知识内容】
                {knowledge}
                
                【用户问题】
                {question}
                
                【回答】
                """;
        String prompt = new PromptTemplate(promptTemplateStr)
                .create(Map.of("knowledge", knowledgeContext, "question", userQuestion))
                .getContents();

        // 3. 调用 ChatClient 的 stream() 方法，返回 Flux<String>
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();  // content() 返回 Flux<String>，每个字符串是一个 token 或片段
    }

}
