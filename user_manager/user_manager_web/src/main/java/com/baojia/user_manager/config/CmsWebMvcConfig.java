package com.baojia.user_manager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class CmsWebMvcConfig implements WebMvcConfigurer {

    private final CmsProperties cmsProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Path.of(cmsProperties.getUploadDir()).toAbsolutePath().normalize();
        String location = "file:" + dir + (dir.toString().endsWith("/") ? "" : "/");
        registry.addResourceHandler("/uploads/baojia-cms/**").addResourceLocations(location);
    }
}
