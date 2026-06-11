package com.baojia.platform.common.dtos;

import lombok.Data;

@Data
@SuppressWarnings("all")
public class AppUserLoginRequestDto {

    private String username;

    private String password;
}
