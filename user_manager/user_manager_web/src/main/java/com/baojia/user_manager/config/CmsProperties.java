package com.baojia.user_manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "baojia.cms")
public class CmsProperties {

    /**
     * 本地上传目录（绝对路径或相对运行目录）
     */
    private String uploadDir = "./uploads/baojia-cms";
}
