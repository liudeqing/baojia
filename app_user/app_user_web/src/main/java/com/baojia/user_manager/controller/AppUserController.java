package com.baojia.user_manager.controller;

import com.baojia.platform.common.ResultModuleVo;
import com.baojia.platform.common.vos.LoginResponseVo;
import com.baojia.user_manager.config.JwtProperties;
import com.baojia.user_manager.model.AppUser;
import com.baojia.user_manager.security.token.JwtTokenService;
import com.baojia.user_manager.service.IAppUserService;
import com.baojia.user_manager_adapter.dto.RegisterAppUserDto;
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
@RequestMapping("/app/user")
@RequiredArgsConstructor
@SuppressWarnings("all")
@Slf4j
public class AppUserController {

    private final JwtTokenService jwtTokenService;

    private final IAppUserService appUserService;

    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    public ResultModuleVo<LoginResponseVo> register(@RequestBody RegisterAppUserDto req, HttpServletRequest request) {
        try {
            AppUser appUser = appUserService.register( req );

            String accessToken = jwtTokenService.createAccessToken(appUser, null);
            LoginResponseVo body = LoginResponseVo.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresInSeconds(jwtProperties.getAccessTokenTtlSeconds())
                    .userId(appUser.getUserId())
                    .username(appUser.getUsername())
                    .roleCodes(null)
                    .build();
            return ResultModuleVo.success( body );
        } catch (RuntimeException e) {
            log.error( e.getMessage() );
            return ResultModuleVo.failure( 300,e.getMessage() );
        }
    }

}
