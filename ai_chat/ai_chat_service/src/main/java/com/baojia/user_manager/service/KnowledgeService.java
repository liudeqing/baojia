package com.baojia.user_manager.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface KnowledgeService {

    /**
     * 加载知识文本：分词 -> 向量化 -> 存入向量库
     */
    public void loadKnowledge(String textContent);

    /**
     * 根据用户问题检索相关文档片段
     */
    public List<Document> retrieveRelevantDocs(String query, int topK) ;
}
