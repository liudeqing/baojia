package com.baojia.user_manager_adapter.dto;

import lombok.Data;

@Data
@SuppressWarnings("all")
public class LoginRequestDto {

    private String username;

    private String password;
}
