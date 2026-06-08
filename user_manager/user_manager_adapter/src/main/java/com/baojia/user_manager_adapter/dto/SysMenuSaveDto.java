package com.baojia.user_manager_adapter.dto;

import lombok.Data;

@Data
public class SysMenuSaveDto {

    /** 为空则新建时由服务端生成 */
    private String menuId;

    private String menuName;

    private String menuType;

    private String menuUrl;

    /** 根节点使用空字符串 */
    private String parentMenuId;

    private Integer status;

    private Integer sortOrder;

    private String remark;
}
