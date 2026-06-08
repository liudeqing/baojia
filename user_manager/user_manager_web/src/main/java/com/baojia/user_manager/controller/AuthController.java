package com.baojia.user_manager.controller;

import com.baojia.platform.common.ResultModuleVo;
import com.baojia.user_manager.config.JwtProperties;
import com.baojia.user_manager.model.SysRole;
import com.baojia.user_manager.model.SysUser;
import com.baojia.user_manager.security.jwt.JwtUserPrincipal;
import com.baojia.user_manager.security.token.JwtTokenService;
import com.baojia.user_manager.security.token.TokenMonitorService;
import com.baojia.user_manager.service.ISysRoleService;
import com.baojia.user_manager.service.ISysUserService;
import com.baojia.user_manager_adapter.dto.LoginRequestDto;
import com.baojia.user_manager_adapter.vo.CurrentUserVo;
import com.baojia.user_manager_adapter.vo.LoginResponseVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * 登录签发 JWT，以及基于令牌解析当前用户（用于联调与监控）。
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class AuthController {

    private final ISysUserService sysUserService;

    private final ISysRoleService roleService;

    private final JwtTokenService jwtTokenService;

    private final TokenMonitorService tokenMonitor;

    private final JwtProperties jwtProperties;

    @PostMapping("/login")
    public ResultModuleVo<LoginResponseVo> login(@RequestBody LoginRequestDto req, HttpServletRequest request) {
        try {
            SysUser user = sysUserService.login(req.getUsername(), req.getPassword());
            List<SysRole> roles = roleService.getRolesByUserId(user.getUserId());
            List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();
            String accessToken = jwtTokenService.createAccessToken(user, roleCodes);
            tokenMonitor.tokenIssued(user.getUserId(), user.getUsername(), jwtProperties.getAccessTokenTtlSeconds());
            sysUserService.updateLoginInfo(user.getUserId(), request.getRemoteAddr());
            LoginResponseVo body = LoginResponseVo.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresInSeconds(jwtProperties.getAccessTokenTtlSeconds())
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .roleCodes(roleCodes)
                    .build();
            return ResultModuleVo.success( body );
        } catch (RuntimeException e) {
            return ResultModuleVo.failure( 300,"登录失败" );
        }
    }

    @GetMapping("/me")
    public ResultModuleVo<CurrentUserVo> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        List<String> roleCodes = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResultModuleVo.success( CurrentUserVo.builder()
                .userId(principal.getUserId())
                .username(principal.getUsername())
                .roleCodes(roleCodes)
                .build()
        );
    }
}
