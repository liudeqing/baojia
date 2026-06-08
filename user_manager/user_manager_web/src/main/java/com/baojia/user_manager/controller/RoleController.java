package com.baojia.user_manager.controller;

import com.baojia.platform.common.ResultModuleVo;
import com.baojia.user_manager.model.SysRole;
import com.baojia.user_manager.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端角色列表（与前端 GET /role/list 对齐）。
 */
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class RoleController {

    private final ISysRoleService roleService;

    @GetMapping("/list")
    public ResultModuleVo<List<SysRole>> listRoles() {
        return ResultModuleVo.success( roleService.list() );
    }
}
