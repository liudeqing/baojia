package com.baojia.user_manager.controller;

import com.alibaba.fastjson.JSON;
import com.baojia.platform.common.ResultModuleVo;
import com.baojia.platform.common.dtos.AppUserLoginRequestDto;
import com.baojia.platform.common.vos.LoginResponseVo;
import com.baojia.user_manager.config.JwtProperties;
import com.baojia.user_manager.model.AppUser;
import com.baojia.user_manager.security.token.JwtTokenService;
import com.baojia.user_manager.security.token.TokenMonitorService;
import com.baojia.user_manager.service.IAppUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录签发 JWT，以及基于令牌解析当前用户（用于联调与监控）。
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SuppressWarnings("all")
@Slf4j
public class AuthController {

    private final JwtTokenService jwtTokenService;

    private final TokenMonitorService tokenMonitor;

    private final JwtProperties jwtProperties;

    private final IAppUserService appUserService;

    @PostMapping("login")
    public ResultModuleVo<LoginResponseVo> login(@RequestBody AppUserLoginRequestDto req, HttpServletRequest request) {
        try {
            log.info( "login,req:{}" , JSON.toJSONString(req) );
            AppUser appUser = appUserService.login( req );
            if ( appUser == null ) {
                return ResultModuleVo.failure( 404 , "暂无用户" );
            }
            String accessToken = jwtTokenService.createAccessToken(appUser, null);
            LoginResponseVo body = LoginResponseVo.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresInSeconds(jwtProperties.getAccessTokenTtlSeconds())
                    .userId(appUser.getUserId())
                    .username(appUser.getUsername())
                    .build();
            return ResultModuleVo.success( body );
        } catch (RuntimeException e) {
            log.error( "login,req:{}" , JSON.toJSONString(req) , e );
            return ResultModuleVo.failure( 300,"登录失败" );
        }
    }

}
