package com.baojia.user_manager_adapter.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SysMenuVo {

    private String menuId;

    private String menuName;

    private String menuType;

    private String menuUrl;

    private String parentMenuId;

    private Integer status;

    private Integer sortOrder;

    private String remark;

    private List<SysMenuVo> children = new ArrayList<>();
}
