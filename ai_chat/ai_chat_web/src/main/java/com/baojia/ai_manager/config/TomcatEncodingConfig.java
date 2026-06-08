package com.baojia.ai_manager.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 说明：Tomcat 按 RFC 3986 校验请求 URI，query 中出现未编码的中文等非 ASCII 字节会直接 400，
 * 请求不会进入 Spring。此类仅保证对已正确 URL 编码的中文参数用 UTF-8 解码。
 */
@Configuration
public class TomcatEncodingConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatUriUtf8() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setURIEncoding("UTF-8");
            connector.setUseBodyEncodingForURI(true);
        });
    }
}
