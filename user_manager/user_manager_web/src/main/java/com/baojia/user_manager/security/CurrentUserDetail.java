package com.baojia.user_manager.security;

import com.baojia.user_manager.security.jwt.JwtUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 从 Spring Security 上下文中读取 JWT 解析后的当前用户 ID。
 * <p>
 * 请求需携带 {@code Authorization: Bearer &lt;token&gt;}，且由 {@link com.baojia.user_manager.security.jwt.JwtAuthenticationFilter} 已写入 {@link SecurityContextHolder}。
 */
public final class CurrentUserDetail {

    private CurrentUserDetail() {
    }

    /**
     * @return 已登录则返回 userId，否则为空（匿名或未认证）
     */
    public static Optional<Long> optional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal.getUserId());
    }

    /**
     * @return 当前用户 ID；未登录时为 {@code null}
     */
    public static Long userIdOrNull() {
        return optional().orElse(null);
    }
}
