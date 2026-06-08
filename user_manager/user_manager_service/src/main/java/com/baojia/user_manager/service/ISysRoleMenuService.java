package com.baojia.user_manager.service;

import com.baojia.user_manager_adapter.dto.RoleMenuBindDto;

import java.util.List;

public interface ISysRoleMenuService {

    List<String> listMenuIdsByRoleId(Long roleId);

    void bindMenus(RoleMenuBindDto dto);
}
