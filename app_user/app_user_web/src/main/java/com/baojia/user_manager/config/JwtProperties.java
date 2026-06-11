package com.baojia.user_manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置。secret 须至少 32 字节（256 bit）以满足 HS256 要求。
 */
@Data
@ConfigurationProperties(prefix = "app.jwt")
@SuppressWarnings("all")
public class JwtProperties {

    /**
     * HMAC 密钥，生产环境务必通过环境变量覆盖，且保持足够长度。
     */
    private String secret = "baojia-app-user-dev-secret-key-min-32-chars!!";

    private long accessTokenTtlSeconds = 7200;

    private String issuer = "baojia_app_user";
}
