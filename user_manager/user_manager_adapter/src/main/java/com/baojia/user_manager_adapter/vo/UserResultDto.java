package com.baojia.user_manager_adapter.vo;

import lombok.Data;

import java.util.List;

/**
 * @author liudeqing
 * @date 2025/9/17
 * @description
 */
@SuppressWarnings("all" )
@Data
public class UserResultDto {

    private Integer userId;

    private String userName;

    private List<UserCustomAttributeDto> customAttributeList;

}
