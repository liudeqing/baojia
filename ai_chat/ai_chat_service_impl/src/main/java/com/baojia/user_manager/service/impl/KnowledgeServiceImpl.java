package com.baojia.user_manager.service.impl;

import com.baojia.user_manager.plugs.FileLoaderUtil;
import com.baojia.user_manager.service.KnowledgeService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings( "all" )
public class KnowledgeServiceImpl implements KnowledgeService {

    private final VectorStore vectorStore;

    @Value("${spring.rag.knowledge-dir}")
    private String knowledgeDir;

    public KnowledgeServiceImpl(EmbeddingModel embeddingModel) {
        this.vectorStore = SimpleVectorStore.builder( embeddingModel ).build();
    }

    /**
     * 启动时自动加载知识库
     */
    @PostConstruct
    public void init() {
        refreshKnowledgeBase();
    }

    /**
     * 刷新知识库：清空现有数据，重新扫描目录并加载
     */
    public synchronized void refreshKnowledgeBase() {
        log.info("开始刷新知识库，目录：{0}", knowledgeDir );
        try {
            List<FileLoaderUtil.DocumentEntry> entries = FileLoaderUtil.loadDocuments(knowledgeDir);
            if (entries.isEmpty()) {
                log.info("⚠️ 知识库目录下没有找到 .txt 或 .md 文件");
                return;
            }
            List<Document> allDocs = entries.stream()
                    .flatMap(entry -> {
                        Document doc = new Document(entry.content);
                        doc.getMetadata().put("source", entry.fileName);
                        log.info( "正在加载{},文件",entry.fileName );
                        TokenTextSplitter splitter = new TokenTextSplitter(300, 50, 5, 10000, true);
                        return splitter.apply(List.of(doc)).stream();
                    })
                    .collect(Collectors.toList());
            vectorStore.add(allDocs);
           log.info("✅ 知识库加载完成，共 " + allDocs.size() + " 个文档片段");
        } catch (IOException e) {
            log.error("❌ 加载知识库失败：" + e.getMessage(), e );
        }
    }

    /**
     * 加载知识文本：分词 -> 向量化 -> 存入向量库
     */
    public void loadKnowledge(String textContent) {
        Document doc = new Document(textContent);
        TokenTextSplitter splitter = new TokenTextSplitter(200, 20, 5, 10000, true);
        List<Document> chunks = splitter.apply(List.of(doc));
        vectorStore.add(chunks);
        log.info("✅ 已加载知识，共 " + chunks.size() + " 个片段");
    }

    /**
     * 根据用户问题检索相关文档片段
     */
    public List<Document> retrieveRelevantDocs(String query, int topK) {
        return vectorStore.similaritySearch(query);
    }

}
