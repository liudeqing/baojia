package com.baojia.user_manager_adapter.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@SuppressWarnings("all")
public class LoginResponseVo {

    private String accessToken;

    private String tokenType;

    private Long expiresInSeconds;

    private Long userId;

    private String username;

    private List<String> roleCodes;
}
