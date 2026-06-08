package com.baojia.user_manager.security.jwt;

import com.baojia.user_manager.security.token.JwtTokenService;
import com.baojia.user_manager.security.token.TokenMonitorService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 从 Authorization: Bearer &lt;token&gt; 解析 JWT，写入 SecurityContext。
 * 校验失败直接返回 401 JSON，便于与网关/前端约定。
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("all")
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private final TokenMonitorService tokenMonitor;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return path.startsWith("/auth/login")
                || path.startsWith("/api/pub/")
                || path.startsWith("/uploads/baojia-cms/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info( "进入授权拦截器" );
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            log.info( "进入拦截器,在需要校验用户token的url中未携带合法的token,请检查Header" );
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "需要校验用户token的url中未携带合法的token,请检查Header");
            return;
        }
        String token = header.substring(7).trim();
        if (token.isEmpty()) {
            log.info( "进入拦截器,在需要校验用户token的url中未携带合法的token,请检查Header" );
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "需要校验用户token的url中携带Token参数缺失,请检查Header");
            return;
        }
        try {
            Claims claims = jwtTokenService.parseAndValidate(token);
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            List<String> roleCodes = readRoleCodes(claims);
            JwtUserPrincipal principal = new JwtUserPrincipal(userId, username, roleCodes);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            tokenMonitor.tokenValidated(userId, username, request.getRequestURI());
            log.info( "Token校验用户凭证通过" );
        } catch (ExpiredJwtException e) {
            log.error( "校验用户凭证时发生异常:" , e.getMessage());
            tokenMonitor.tokenInvalid(request.getRequestURI(), "expired", e);
            SecurityContextHolder.clearContext();
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "访问令牌已过期");
            return;
        } catch (JwtException e) {
            log.error( "校验用户凭证时发生异常:" , e.getMessage());
            tokenMonitor.tokenInvalid(request.getRequestURI(), "invalid", e);
            SecurityContextHolder.clearContext();
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "访问令牌无效");
            return;
        } catch ( Exception e ) {
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    @SuppressWarnings("unchecked")
    private static List<String> readRoleCodes(Claims claims) {
        Object raw = claims.get("roleCodes");
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object o : list) {
                if (o != null) {
                    out.add(String.valueOf(o));
                }
            }
            return out;
        }
        return List.of();
    }

    private static void writeJson(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + status + ",\"message\":\"" + message + "\"}");
    }
}
