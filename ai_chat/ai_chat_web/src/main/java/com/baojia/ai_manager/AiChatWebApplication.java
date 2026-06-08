package com.baojia.ai_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.baojia.**",
})
public class AiChatWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiChatWebApplication.class, args);
    }

}
