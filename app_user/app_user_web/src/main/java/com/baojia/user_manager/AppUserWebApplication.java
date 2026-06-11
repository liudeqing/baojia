package com.baojia.user_manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.baojia.**",
})
@MapperScan({
        "com.baojia.user_manager.mapper",
        "com.baojia.platform.dict.mapper",
})
public class AppUserWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppUserWebApplication.class, args);
    }

}
