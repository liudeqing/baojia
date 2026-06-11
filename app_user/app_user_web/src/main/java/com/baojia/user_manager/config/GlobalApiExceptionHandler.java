package com.baojia.user_manager.config;

import com.baojia.platform.common.ResultModuleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * 将未在控制器内捕获的异常转为统一 JSON，避免仅依赖默认 /error 流程。
 */
@RestControllerAdvice
@Slf4j
@SuppressWarnings("all")
public class GlobalApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResultModuleVo<Void> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String msg = ex.getReason() != null ? ex.getReason() : (ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase());
        return ResultModuleVo.failure(status.value(), msg);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResultModuleVo<Void> handleRuntime(RuntimeException ex) {
        log.warn("业务异常: {}", ex.toString());
        return ResultModuleVo.failure(500, ex.getMessage() == null ? "服务器内部错误" : ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultModuleVo<Void> handleException(Exception ex) {
        log.error("未处理异常", ex);
        return ResultModuleVo.failure(500, ex.getMessage() == null ? "服务器内部错误" : ex.getMessage());
    }
}
