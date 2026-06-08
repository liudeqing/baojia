package com.baojia.user_manager_adapter.vo;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("all")
public class UserQueryItemVo {

    private Long userId;

    private String username;

    private String realName;

    private String phone;

    private Long orgId;

    private String orgName;

    private List<Long> roleIds;
}
