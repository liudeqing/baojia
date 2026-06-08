package com.baojia.user_manager_adapter.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleMenuBindDto {

    private Long roleId;

    private List<String> menuIds;
}
