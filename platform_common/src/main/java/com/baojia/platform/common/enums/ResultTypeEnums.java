package com.baojia.platform.common.enums;

/**
 * 结果集返回类型
 */
public enum ResultTypeEnums {

    NORMAL("普通行为"),

    REDIRECT("跳转行为");

    private final String description;

    ResultTypeEnums(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
