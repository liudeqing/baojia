package com.baojia.user_manager_adapter.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author liudeqing
 * @date 2025/9/17
 * @description
 */
@SuppressWarnings( "all" )
@Data
@Builder
public class UserCustomAttributeDto {

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性值
     */
    private String value;

}
