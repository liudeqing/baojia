package com.baojia.user_manager.service;

import com.baojia.user_manager.model.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysUserRoleService extends IService<SysUserRole> {

    boolean removeByUserId(Long userId);

    boolean removeByOrgId(Long orgId);

    List<Long> selectRoleIdsByUserId(Long userId);

    List<Long> selectUserIdsByRoleId(Long roleId);

}
