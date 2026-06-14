package com.baojia.user_manager.controller;

import com.baojia.platform.common.ResultModuleVo;
import com.baojia.platform.common.vos.CurrentUserVo;
import com.baojia.platform.common.vos.LoginResponseVo;
import com.baojia.user_manager.config.JwtProperties;
import com.baojia.user_manager.model.AppUser;
import com.baojia.user_manager.security.jwt.JwtUserPrincipal;
import com.baojia.user_manager.security.token.JwtTokenService;
import com.baojia.user_manager.service.IAppUserService;
import com.baojia.user_manager_adapter.dto.RegisterAppUserDto;
import com.baojia.user_manager_adapter.dto.UpdateAppUserDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping("/detail")
    public ResultModuleVo<CurrentUserVo> detail() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal principal)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
            }
            AppUser appUser = appUserService.getById( principal.getUserId() );
            return ResultModuleVo.success( CurrentUserVo.builder()
                    .userId(principal.getUserId())
                    .username(principal.getUsername())
                    .phone(appUser.getPhone())
                    .email( appUser.getEmail() )
                    .avatar( appUser.getAvatar() )
                    .nickname( appUser.getNickname() )
                    .email( appUser.getEmail() )
                    .birthday( appUser.getBirthday() )
                    .gender( appUser.getGender() )
                    .realName( appUser.getRealName() )
                    .build()
            );
        } catch (RuntimeException e) {
            log.error( e.getMessage() );
            return ResultModuleVo.failure( 300,e.getMessage() );
        }
    }

    @PostMapping("/update")
    public ResultModuleVo<CurrentUserVo> update( @RequestBody UpdateAppUserDetail updateAppUserDetail ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal principal)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
            }
            AppUser appUser = appUserService.getById( principal.getUserId() );
            if( appUser == null ) {
                return ResultModuleVo.failure( 300, "用户不存在" );
            }
            appUser.setRealName( updateAppUserDetail.getRealName() );
            appUser.setUpdateBy( appUser.getUserId() );
            appUser.setUpdateTime(LocalDateTime.now() );
            appUser.setUsername( updateAppUserDetail.getUsername() );
            appUser.setNickname( updateAppUserDetail.getNickname() );
            appUser.setAvatar( updateAppUserDetail.getAvatar() );
            appUser.setPhone( updateAppUserDetail.getPhone() );
            appUser.setEmail( updateAppUserDetail.getEmail() );
            appUser.setBirthday( updateAppUserDetail.getBirthday() );
            appUser.setGender( updateAppUserDetail.getGender() );
            appUser.setBirthday( updateAppUserDetail.getBirthday() );
            appUserService.updateById( appUser );
            return ResultModuleVo.success( CurrentUserVo.builder()
                    .userId(principal.getUserId())
                    .username(principal.getUsername())
                    .phone(appUser.getPhone())
                    .email( appUser.getEmail() )
                    .avatar( appUser.getAvatar() )
                    .nickname( appUser.getNickname() )
                    .realName( appUser.getRealName() )
                    .birthday( appUser.getBirthday() )
                    .gender( appUser.getGender() )
                    .build()
            );
        } catch (RuntimeException e) {
            log.error( e.getMessage() );
            return ResultModuleVo.failure( 300,e.getMessage() );
        }
    }

}
