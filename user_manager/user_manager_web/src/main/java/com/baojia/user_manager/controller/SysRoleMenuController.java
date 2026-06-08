package com.baojia.user_manager.controller;

import com.baojia.platform.common.ResultModuleVo;
import com.baojia.user_manager.service.ISysRoleMenuService;
import com.baojia.user_manager_adapter.dto.RoleMenuBindDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色与菜单关联；统一 {@link ResultModule}。
 */
@RestController
@RequestMapping("/body/sys/role-menus")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class SysRoleMenuController {

    private final ISysRoleMenuService sysRoleMenuService;

    @GetMapping("/{roleId}")
    public ResultModuleVo<List<String>> listMenuIds(@PathVariable("roleId") Long roleId) {
        try {
            return ResultModuleVo.success(sysRoleMenuService.listMenuIdsByRoleId(roleId));
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @PostMapping("/bind")
    public ResultModuleVo<Boolean> bind(@RequestBody RoleMenuBindDto dto) {
        try {
            sysRoleMenuService.bindMenus(dto);
            return ResultModuleVo.success(Boolean.TRUE);
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }
}
