package com.baojia.user_manager.service;

import com.baojia.user_manager.model.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {

    /**
     * 创建角色
     */
    SysRole createRole(SysRole role);

    /**
     * 更新角色
     */
    SysRole updateRole(SysRole role);

    /**
     * 删除角色（逻辑删除，同时检查是否有用户关联）
     */
    boolean deleteRole(Long roleId);

    /**
     * 获取角色详情
     */
    SysRole getRoleById(Long roleId);

    /**
     * 根据角色编码获取角色
     */
    SysRole getRoleByCode(String roleCode);

    /**
     * 更新角色状态
     */
    boolean updateRoleStatus(Long roleId, Integer status);

    /**
     * 获取组织下的角色列表
     */
    List<SysRole> getRolesByOrgId(Long orgId);

    /**
     * 获取用户拥有的角色
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 检查角色编码是否存在
     */
    boolean checkRoleCodeExists(String roleCode, Long excludeRoleId);
}
