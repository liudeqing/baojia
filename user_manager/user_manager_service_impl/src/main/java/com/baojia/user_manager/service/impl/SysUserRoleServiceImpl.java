package com.baojia.user_manager.service.impl;

import com.baojia.user_manager.mapper.SysUserRoleMapper;
import com.baojia.user_manager.model.SysUserRole;
import com.baojia.user_manager.service.ISysUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@SuppressWarnings( "all" )
@Slf4j
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByUserId(Long userId) {
        return baseMapper.deleteByUserId(userId) >= 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByOrgId(Long orgId) {
        return baseMapper.deleteByOrgId(orgId) >= 0;
    }

    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return baseMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public List<Long> selectUserIdsByRoleId(Long roleId) {
        return baseMapper.selectUserIdsByRoleId( roleId );
    }

    @Override
    public boolean saveBatch(Collection<SysUserRole> entityList) {
        return super.saveBatch(entityList);
    }
}
