package com.baojia.platform.common;

import com.baojia.platform.common.enums.ResultTypeEnums;
import lombok.Data;

/**
 * 公共统一返回（各业务模块共用）。
 */
@Data
@SuppressWarnings("all")
public class ResultModuleVo<T> {

    private Integer code;

    private String message;

    private ResultTypeEnums resultTypeEnum;

    private T data;

    private String getResultTypeDescription() {
        return resultTypeEnum != null ? resultTypeEnum.getDescription() : "";
    }

    private ResultModuleVo(Integer code, String message, ResultTypeEnums resultTypeEnum, T data) {
        this.code = code;
        this.message = message;
        this.resultTypeEnum = resultTypeEnum;
        this.data = data;
    }

    public static <T> ResultModuleVo<T> success(T data) {
        return new ResultModuleVo<>(200, "操作成功", ResultTypeEnums.NORMAL, data);
    }

    public static <T> ResultModuleVo<T> failure(int code, String message) {
        return new ResultModuleVo<>(code, message == null ? "操作失败" : message, ResultTypeEnums.NORMAL, null);
    }
}
