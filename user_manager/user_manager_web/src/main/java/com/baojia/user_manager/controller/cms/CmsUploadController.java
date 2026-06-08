package com.baojia.user_manager.controller.cms;

import com.baojia.user_manager.config.CmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * 文章图片上传（需登录）；返回相对 URL 供富文本/配图使用。
 */
@RestController
@RequestMapping("/body/cms")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class CmsUploadController {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final CmsProperties cmsProperties;

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("请选择文件");
        }
        String original = file.getOriginalFilename();
        String ext = "";
        if (StringUtils.hasText(original) && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.')).toLowerCase();
        }
        if (!ext.matches("\\.(jpg|jpeg|png|gif|webp|bmp)")) {
            throw new RuntimeException("仅支持常见图片格式：jpg/png/gif/webp/bmp");
        }
        Path base = Path.of(cmsProperties.getUploadDir()).toAbsolutePath().normalize();
        String sub = LocalDate.now().format(DAY);
        Path dayDir = base.resolve(sub);
        Files.createDirectories(dayDir);
        String name = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dayDir.resolve(name);
        file.transferTo(target.toFile());
        String url = "/uploads/baojia-cms/" + sub + "/" + name;
        return Map.of("url", url.replace("\\", "/"));
    }
}
