package com.baojia.user_manager.service.impl;

import com.baojia.user_manager.mapper.SysRoleMenuMapper;
import com.baojia.user_manager.model.SysRoleMenu;
import com.baojia.user_manager.service.ISysRoleMenuService;
import com.baojia.user_manager_adapter.dto.RoleMenuBindDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

    @Override
    public List<String> listMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return List.of();
        }
        return list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)).stream()
                .map(SysRoleMenu::getMenuId)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindMenus(RoleMenuBindDto dto) {
        if (dto == null || dto.getRoleId() == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, dto.getRoleId()));
        if (CollectionUtils.isEmpty(dto.getMenuIds())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (String mid : dto.getMenuIds()) {
            if (mid == null || mid.isBlank()) {
                continue;
            }
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(dto.getRoleId());
            rm.setMenuId(mid.trim());
            rm.setCreateTime(now);
            save(rm);
        }
    }
}
