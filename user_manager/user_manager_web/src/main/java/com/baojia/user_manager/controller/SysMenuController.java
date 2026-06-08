package com.baojia.user_manager.controller;

import com.baojia.platform.common.ResultModuleVo;
import com.baojia.user_manager.model.SysMenu;
import com.baojia.user_manager.security.CurrentUserDetail;
import com.baojia.user_manager.service.ISysMenuService;
import com.baojia.user_manager_adapter.dto.SysMenuSaveDto;
import com.baojia.user_manager_adapter.vo.SysMenuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理；统一 {@link ResultModule}，业务数据在 {@code data} 字段。
 */
@RestController
@RequestMapping("/body/sys/menus")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class SysMenuController {

    private final ISysMenuService sysMenuService;

    @GetMapping("/tree")
    public ResultModuleVo<List<SysMenuVo>> treeAll() {
        try {
            return ResultModuleVo.success(sysMenuService.listTreeAll());
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @GetMapping("/tree-for-me")
    public ResultModuleVo<List<SysMenuVo>> treeForMe() {
        try {
            Long uid = CurrentUserDetail.userIdOrNull();
            return ResultModuleVo.success(sysMenuService.listTreeForUser(uid));
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResultModuleVo<SysMenu> save(@RequestBody SysMenuSaveDto dto) {
        try {
            Long uid = CurrentUserDetail.userIdOrNull();
            return ResultModuleVo.success(sysMenuService.save(dto, uid));
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }

    @DeleteMapping("/{menuId}")
    public ResultModuleVo<Boolean> delete(@PathVariable("menuId") String menuId) {
        try {
            sysMenuService.deleteByMenuId(menuId);
            return ResultModuleVo.success(Boolean.TRUE);
        } catch (RuntimeException e) {
            return ResultModuleVo.failure(500, e.getMessage());
        }
    }
}
