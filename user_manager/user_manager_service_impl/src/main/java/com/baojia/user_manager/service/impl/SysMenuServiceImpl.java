package com.baojia.user_manager.service.impl;

import com.baojia.user_manager.mapper.SysMenuMapper;
import com.baojia.user_manager.mapper.SysRoleMenuMapper;
import com.baojia.user_manager.model.SysMenu;
import com.baojia.user_manager.model.SysRoleMenu;
import com.baojia.user_manager.model.SysRole;
import com.baojia.user_manager.service.ISysMenuService;
import com.baojia.user_manager.service.ISysRoleService;
import com.baojia.user_manager_adapter.dto.SysMenuSaveDto;
import com.baojia.user_manager_adapter.vo.SysMenuVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private static final String ADMIN_ROLE_CODE = "ROLE_ADMIN";

    private final SysRoleMenuMapper sysRoleMenuMapper;

    private final ISysRoleService sysRoleService;

    @Override
    public List<SysMenuVo> listTreeAll() {
        List<SysMenu> flat = list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSortOrder).orderByAsc(SysMenu::getMenuId));
        return buildTree(flat);
    }

    @Override
    public List<SysMenuVo> listTreeForUser(Long userId) {
        if (userId == null) {
            return List.of();
        }
        List<SysRole> roles = sysRoleService.getRolesByUserId(userId);
        boolean admin = roles.stream().anyMatch(r -> ADMIN_ROLE_CODE.equals(r.getRoleCode()));
        List<SysMenu> flat;
        if (admin) {
            flat = list(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSortOrder)
                    .orderByAsc(SysMenu::getMenuId));
        } else {
            Set<Long> roleIds = roles.stream().map(SysRole::getRoleId).filter(Objects::nonNull).collect(Collectors.toSet());
            if (roleIds.isEmpty()) {
                return List.of();
            }
            List<SysRoleMenu> links = sysRoleMenuMapper.selectList(
                    new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
            Set<String> menuIds = links.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
            if (menuIds.isEmpty()) {
                return List.of();
            }
            flat = list(new LambdaQueryWrapper<SysMenu>()
                    .in(SysMenu::getMenuId, menuIds)
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSortOrder)
                    .orderByAsc(SysMenu::getMenuId));
            flat = ensureParentChain(flat);
        }
        return buildTree(flat);
    }

    /**
     * 子菜单选中时需展示父级：把缺失的父节点补进列表（仅启用）
     */
    private List<SysMenu> ensureParentChain(List<SysMenu> menus) {
        Map<String, SysMenu> byId = menus.stream().collect(Collectors.toMap(SysMenu::getMenuId, m -> m, (a, b) -> a));
        Set<String> need = new HashSet<>();
        for (SysMenu m : menus) {
            String p = m.getParentMenuId();
            while (StringUtils.hasText(p) && !byId.containsKey(p)) {
                need.add(p);
                SysMenu parent = getById(p);
                if (parent == null) {
                    break;
                }
                p = parent.getParentMenuId();
            }
        }
        if (need.isEmpty()) {
            return menus;
        }
        List<SysMenu> extra = list(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getMenuId, need).eq(SysMenu::getStatus, 1));
        List<SysMenu> merged = new ArrayList<>(menus);
        Set<String> have = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toSet());
        for (SysMenu e : extra) {
            if (!have.contains(e.getMenuId())) {
                merged.add(e);
                have.add(e.getMenuId());
            }
        }
        merged.sort(Comparator.comparing(SysMenu::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SysMenu::getMenuId));
        return merged;
    }

    private List<SysMenuVo> buildTree(List<SysMenu> flat) {
        Map<String, SysMenuVo> nodes = new LinkedHashMap<>();
        for (SysMenu m : flat) {
            nodes.put(m.getMenuId(), toVo(m));
        }
        List<SysMenuVo> roots = new ArrayList<>();
        for (SysMenu m : flat) {
            SysMenuVo vo = nodes.get(m.getMenuId());
            String pid = m.getParentMenuId() == null ? "" : m.getParentMenuId();
            if (!StringUtils.hasText(pid)) {
                roots.add(vo);
            } else {
                SysMenuVo parent = nodes.get(pid);
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        return roots;
    }

    private static SysMenuVo toVo(SysMenu m) {
        SysMenuVo vo = new SysMenuVo();
        vo.setMenuId(m.getMenuId());
        vo.setMenuName(m.getMenuName());
        vo.setMenuType(m.getMenuType());
        vo.setMenuUrl(m.getMenuUrl());
        vo.setParentMenuId(m.getParentMenuId());
        vo.setStatus(m.getStatus());
        vo.setSortOrder(m.getSortOrder());
        vo.setRemark(m.getRemark());
        vo.setChildren(new ArrayList<>());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysMenu save(SysMenuSaveDto dto, Long operatorUserId) {
        if (dto == null || !StringUtils.hasText(dto.getMenuName())) {
            throw new RuntimeException("菜单名称不能为空");
        }
        if (!StringUtils.hasText(dto.getMenuType())) {
            throw new RuntimeException("菜单类型不能为空");
        }
        String menuUrl = dto.getMenuUrl() != null ? dto.getMenuUrl() : "";
        String parentId = dto.getParentMenuId() != null ? dto.getParentMenuId().trim() : "";
        SysMenu entity;
        if (!StringUtils.hasText(dto.getMenuId())) {
            entity = new SysMenu();
            entity.setMenuId("m" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
            entity.setCreateTime(LocalDateTime.now());
            entity.setCreateBy(operatorUserId);
        } else {
            entity = getById(dto.getMenuId().trim());
            if (entity == null) {
                throw new RuntimeException("菜单不存在");
            }
        }
        if (StringUtils.hasText(parentId) && parentId.equals(entity.getMenuId())) {
            throw new RuntimeException("父菜单不能为当前菜单自身");
        }
        if (StringUtils.hasText(parentId)) {
            SysMenu p = getById(parentId);
            if (p == null) {
                throw new RuntimeException("父菜单不存在");
            }
        }
        entity.setMenuName(dto.getMenuName().trim());
        entity.setMenuType(dto.getMenuType().trim());
        entity.setMenuUrl(menuUrl);
        entity.setParentMenuId(parentId);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setRemark(dto.getRemark());
        entity.setUpdateBy(operatorUserId);
        entity.setUpdateTime(LocalDateTime.now());
        if (entity.getCreateTime() == null) {
            entity.setCreateBy(operatorUserId);
            entity.setCreateTime(LocalDateTime.now());
        }
        saveOrUpdate(entity);
        return getById(entity.getMenuId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByMenuId(String menuId) {
        if (!StringUtils.hasText(menuId)) {
            throw new RuntimeException("菜单ID不能为空");
        }
        removeById(menuId.trim());
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId.trim()));
    }
}
