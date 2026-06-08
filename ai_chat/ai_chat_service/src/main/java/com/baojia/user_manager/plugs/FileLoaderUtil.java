package com.baojia.user_manager.plugs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * rag 文件加载器具 用来加载想要加载的知识库信息
 *
 */
public class FileLoaderUtil {
    /**
     * 递归扫描目录下所有 .txt 和 .md 文件，读取内容
     * @param dirPath 目录路径
     * @return 列表，每个元素为 {fileName, content}
     */
    public static List<DocumentEntry> loadDocuments(String dirPath) throws IOException {
        List<DocumentEntry> entries = new ArrayList<>();
        Path root = Paths.get(dirPath);
        if (!Files.exists(root)) {
            Files.createDirectories(root);
            return entries;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt") || p.toString().endsWith(".md"))
                    .forEach(p -> {
                        try {
                            String content = Files.readString(p, StandardCharsets.UTF_8);
                            entries.add(new DocumentEntry(p.toString(), content));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return entries;
    }

    public static class DocumentEntry {
        public final String fileName;
        public final String content;
        public DocumentEntry(String fileName, String content) {
            this.fileName = fileName;
            this.content = content;
        }
    }
}
