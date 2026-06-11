package com.baojia.user_manager.security.token;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Token 生命周期监控：签发、校验成功、校验失败（含过期、签名错误等），便于审计与排障。
 * 日志名独立，可在 logback 中单独输出到文件。
 */
@Component
@Slf4j(topic = "com.baojia.user_manager.security.token.TokenMonitor")
@SuppressWarnings("all")
public class TokenMonitorService {

    public void tokenIssued(Long userId, String username, long expiresInSeconds) {
        log.info("TOKEN_ISSUED userId={} username={} expiresInSeconds={}", userId, username, expiresInSeconds);
    }

    public void tokenValidated(Long userId, String username, String path) {
        log.debug("TOKEN_OK userId={} username={} path={}", userId, username, path);
    }

    public void tokenInvalid(String path, String reason, Exception ex) {
        if (ex instanceof ExpiredJwtException) {
            log.warn("TOKEN_EXPIRED path={} msg={}", path, ex.getMessage());
        } else if (ex instanceof JwtException) {
            log.warn("TOKEN_INVALID path={} reason={} msg={}", path, reason, ex.getMessage());
        } else {
            log.warn("TOKEN_ERROR path={} reason={}", path, reason, ex);
        }
    }

    public void tokenMissingBearer(String path) {
        log.debug("TOKEN_MISSING path={} (protected resource requires Authorization: Bearer)", path);
    }
}
