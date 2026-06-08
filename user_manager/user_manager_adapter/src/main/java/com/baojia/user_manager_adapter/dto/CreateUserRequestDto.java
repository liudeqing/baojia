package com.baojia.user_manager_adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateUserRequestDto {

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String email;

    private Long orgId;

    private List<Long> roleIds;
}
