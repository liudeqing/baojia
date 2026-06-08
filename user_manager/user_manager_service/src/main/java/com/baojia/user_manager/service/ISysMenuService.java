package com.baojia.user_manager.service;

import com.baojia.user_manager.model.SysMenu;
import com.baojia.user_manager_adapter.dto.SysMenuSaveDto;
import com.baojia.user_manager_adapter.vo.SysMenuVo;

import java.util.List;

public interface ISysMenuService {

    /** 全部菜单树（管理端，含禁用） */
    List<SysMenuVo> listTreeAll();

    /** 当前用户可见菜单树（仅启用） */
    List<SysMenuVo> listTreeForUser(Long userId);

    SysMenu save(SysMenuSaveDto dto, Long operatorUserId);

    void deleteByMenuId(String menuId);
}
