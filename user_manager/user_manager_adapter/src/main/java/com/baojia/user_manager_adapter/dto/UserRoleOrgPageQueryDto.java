package com.baojia.user_manager_adapter.dto;

import lombok.Data;

@Data
@SuppressWarnings("all")
public class UserRoleOrgPageQueryDto {

    /**
     * 角色ID（可选）
     */
    private Long roleId;

    /**
     * 组织ID（可选）
     */
    private Long orgId;

    /**
     * 页码，从1开始
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
