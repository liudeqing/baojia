package com.baojia.user_manager.service.impl;


import com.baojia.user_manager.mapper.SysRoleMapper;
import com.baojia.user_manager.model.SysRole;
import com.baojia.user_manager.service.ISysOrganizationService;
import com.baojia.user_manager.service.ISysRoleService;
import com.baojia.user_manager.service.ISysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final ISysUserRoleService sysUserRoleService;

    private final ISysOrganizationService organizationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole createRole(SysRole role) {
        // 检查角色编码是否存在
        if (checkRoleCodeExists(role.getRoleCode(), null)) {
            throw new RuntimeException("角色编码已存在");
        }

        // 检查组织是否存在
        if (role.getOrgId() != null) {
            if (organizationService.getById(role.getOrgId()) == null) {
                throw new RuntimeException("组织不存在");
            }
        }

        role.setStatus(1);
        save(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole updateRole(SysRole role) {
        SysRole existingRole = getById(role.getRoleId());
        if (existingRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 检查角色编码是否存在
        if (StringUtils.hasText(role.getRoleCode())
                && !role.getRoleCode().equals(existingRole.getRoleCode())) {
            if (checkRoleCodeExists(role.getRoleCode(), role.getRoleId())) {
                throw new RuntimeException("角色编码已存在");
            }
        }

        updateById(role);
        return getById(role.getRoleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        // 检查角色下是否有用户
        List<Long> userIds = sysUserRoleService.selectUserIdsByRoleId(roleId);
        if (!userIds.isEmpty()) {
            throw new RuntimeException("该角色下还有用户，无法删除");
        }

        return removeById(roleId);
    }

    @Override
    public SysRole getRoleById(Long roleId) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return role;
    }

    @Override
    public SysRole getRoleByCode(String roleCode) {
        return baseMapper.selectByRoleCode(roleCode);
    }

    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setStatus(status);
        return updateById(role);
    }

    @Override
    public List<SysRole> getRolesByOrgId(Long orgId) {
        return baseMapper.selectRolesByOrgId(orgId);
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public boolean checkRoleCodeExists(String roleCode, Long excludeRoleId) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode)
                .eq(SysRole::getDeleted, 0);
        if (excludeRoleId != null) {
            wrapper.ne(SysRole::getRoleId, excludeRoleId);
        }
        return count(wrapper) > 0;
    }
}
