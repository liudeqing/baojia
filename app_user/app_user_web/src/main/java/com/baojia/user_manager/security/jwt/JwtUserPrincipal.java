package com.baojia.user_manager.security.jwt;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * JWT 解析后的当前登录用户（放入 SecurityContext）。
 */
@Getter
@SuppressWarnings("all")
public class JwtUserPrincipal implements UserDetails {

    private final Long userId;

    private final String username;

    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserPrincipal(Long userId, String username, List<String> roleCodes) {
        this.userId = userId;
        this.username = username;
        this.authorities = roleCodes == null ? List.of()
                : roleCodes.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
