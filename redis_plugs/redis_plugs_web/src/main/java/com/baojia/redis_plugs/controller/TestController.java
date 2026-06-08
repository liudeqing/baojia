package com.baojia.redis_plugs.controller;

import com.baojia.redis_plugs.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * @author liudeqing
 * @date 2025/9/11
 * @description
 */
@Controller
@RequestMapping( "/test" )
@SuppressWarnings( "all" )
public class TestController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping( value = "put")
    @ResponseBody
    public String TestPut( @RequestParam("key") String key, @RequestParam( "value" ) String value ) {
        redisTemplate.opsForValue().set("user:" + key, value, 1, TimeUnit.HOURS);
        redisUtil.set("user:info:" + key, value, 3600, TimeUnit.SECONDS);
        return super.toString();
    }
}
