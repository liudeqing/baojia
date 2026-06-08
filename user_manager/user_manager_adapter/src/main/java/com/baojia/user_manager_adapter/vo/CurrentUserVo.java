package com.baojia.user_manager_adapter.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@SuppressWarnings("all")
public class CurrentUserVo {

    private Long userId;

    private String username;

    private List<String> roleCodes;
}
