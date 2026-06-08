package com.baojia.user_manager.service.impl;

import com.baojia.user_manager.mapper.SysOrganizationMapper;
import com.baojia.user_manager.model.SysOrganization;
import com.baojia.user_manager.model.SysUser;
import com.baojia.user_manager.service.ISysOrganizationService;
import com.baojia.user_manager.service.ISysUserService;
import com.baojia.user_manager_adapter.vo.SysOrganizationTreeDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings( "all" )
@RequiredArgsConstructor
public class SysOrganizationServiceImpl extends ServiceImpl<SysOrganizationMapper, SysOrganization>
        implements ISysOrganizationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysOrganization createOrganization(SysOrganization organization) {
        // 检查组织编码是否存在
        if (checkOrgCodeExists(organization.getOrgCode(), null)) {
            throw new RuntimeException("组织编码已存在");
        }

        // 设置组织层级和路径
        if (organization.getParentOrgId() != null && organization.getParentOrgId() > 0) {
            SysOrganization parentOrg = getById(organization.getParentOrgId());
            if (parentOrg == null) {
                throw new RuntimeException("父组织不存在");
            }
            organization.setOrgLevel(parentOrg.getOrgLevel() + 1);
            organization.setOrgPath(parentOrg.getOrgPath() + "/" + organization.getOrgId());
        } else {
            organization.setOrgLevel(1);
            organization.setParentOrgId(0L);
        }

        organization.setStatus(1);
        save(organization);

        // 更新组织路径
        if (organization.getOrgPath() == null) {
            String orgPath = "/" + organization.getOrgId();
            organization.setOrgPath(orgPath);
            baseMapper.updateOrgPath(organization.getOrgId(), orgPath);
        }

        return organization;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysOrganization updateOrganization(SysOrganization organization) {
        SysOrganization existingOrg = getById(organization.getOrgId());
        if (existingOrg == null) {
            throw new RuntimeException("组织不存在");
        }

        // 检查组织编码是否存在
        if (StringUtils.hasText(organization.getOrgCode())
                && !organization.getOrgCode().equals(existingOrg.getOrgCode())) {
            if (checkOrgCodeExists(organization.getOrgCode(), organization.getOrgId())) {
                throw new RuntimeException("组织编码已存在");
            }
        }

        updateById(organization);
        return getById(organization.getOrgId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrganization(Long orgId,Long operatorUserId) {
        // 检查是否有子组织
        LambdaQueryWrapper<SysOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrganization::getParentOrgId, orgId)
                .eq(SysOrganization::getDeleted, 0);
        long childCount = count(wrapper);
        if (childCount > 0) {
            throw new RuntimeException("请先删除所有子组织");
        }

        // 检查组织下是否有用户
        /*Long userCount = sysUserService.count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getOrgId, orgId)
                .eq(SysUser::getDeleted, 0));
        if (userCount > 0) {
            throw new RuntimeException("请先删除或转移组织下的用户");
        }*/
        SysOrganization old = getOrganizationById(orgId);
        old.setUpdateTime( LocalDateTime.now() );
        old.setDeleted( 1 );
        old.setUpdateBy( operatorUserId );
        return updateById( old );
    }

    @Override
    public SysOrganization getOrganizationById(Long orgId) {
        SysOrganization organization = getById(orgId);
        if (organization == null) {
            throw new RuntimeException("组织不存在");
        }
        return organization;
    }

    @Override
    public List<SysOrganizationTreeDto> getOrganizationTree() {
        LambdaQueryWrapper<SysOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrganization::getDeleted, 0)
                .orderByAsc(SysOrganization::getSortOrder)
                .orderByAsc(SysOrganization::getCreateTime);
        List<SysOrganization> allOrgs = list(wrapper);
        List<SysOrganizationTreeDto> organizationTreeDtos = new ArrayList<>();
        allOrgs.forEach(organization -> {
            SysOrganizationTreeDto dto = new SysOrganizationTreeDto();
            BeanUtils.copyProperties(organization, dto);
            organizationTreeDtos.add( dto );
        });
        SysOrganizationTreeDto root = organizationTreeDtos.stream().filter( s-> s.getParentOrgId() == null ).findFirst().get();
        root.setChildren( buildOrgTree( organizationTreeDtos.stream().filter( s->s.getParentOrgId() != null ).collect( Collectors.toList() ) , root.getOrgId()) );
        root.setParentOrgId( 0L );
        return Arrays.asList( root );
    }

    @Override
    public List<Long> getOrgAndChildrenIds(Long orgId) {
        List<Long> orgIds = new ArrayList<>();
        orgIds.add(orgId);

        List<SysOrganization> children = list(new LambdaQueryWrapper<SysOrganization>()
                .eq(SysOrganization::getParentOrgId, orgId)
                .eq(SysOrganization::getDeleted, 0));

        for (SysOrganization child : children) {
            orgIds.addAll(getOrgAndChildrenIds(child.getOrgId()));
        }

        return orgIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean moveOrganization(Long orgId, Long newParentId) {
        SysOrganization organization = getById(orgId);
        if (organization == null) {
            throw new RuntimeException("组织不存在");
        }

        // 检查不能移动到自己的子组织下
        if (newParentId != null && newParentId > 0) {
            List<Long> childrenIds = getOrgAndChildrenIds(orgId);
            if (childrenIds.contains(newParentId)) {
                throw new RuntimeException("不能移动到自己的子组织下");
            }
        }

        organization.setParentOrgId(newParentId);

        // 重新计算组织层级和路径
        if (newParentId != null && newParentId > 0) {
            SysOrganization parentOrg = getById(newParentId);
            organization.setOrgLevel(parentOrg.getOrgLevel() + 1);
            organization.setOrgPath(parentOrg.getOrgPath() + "/" + orgId);
        } else {
            organization.setOrgLevel(1);
            organization.setParentOrgId(0L);
            organization.setOrgPath("/" + orgId);
        }

        updateById(organization);

        // 更新所有子组织的层级和路径
        updateChildrenOrgPath(organization.getOrgId(), organization.getOrgPath(), organization.getOrgLevel());

        return true;
    }

    @Override
    public boolean updateOrganizationStatus(Long orgId, Integer status) {
        SysOrganization organization = getById(orgId);
        if (organization == null) {
            throw new RuntimeException("组织不存在");
        }
        organization.setStatus(status);
        return updateById(organization);
    }

    @Override
    public boolean checkOrgCodeExists(String orgCode, Long excludeOrgId) {
        LambdaQueryWrapper<SysOrganization> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysOrganization::getOrgCode, orgCode)
                .eq(SysOrganization::getDeleted, 0);
        if (excludeOrgId != null) {
            wrapper.ne(SysOrganization::getOrgId, excludeOrgId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public int countUsersByOrgId(Long orgId) {
        return baseMapper.countUsersByOrgId(orgId);
    }

    /**
     * 构建组织树
     */
    private List<SysOrganizationTreeDto> buildOrgTree(List<SysOrganizationTreeDto> allOrgs, Long parentId) {
        return allOrgs.stream()
                .filter(org -> org.getParentOrgId() != null && org.getParentOrgId().equals(parentId))
                .map(org -> {
                    org.setChildren(buildOrgTree(allOrgs, org.getOrgId()));
                    return org;
                })
                .collect(Collectors.toList());
    }

    /**
     * 更新子组织的路径和层级
     */
    private void updateChildrenOrgPath(Long parentId, String parentPath, int parentLevel) {
        List<SysOrganization> children = list(new LambdaQueryWrapper<SysOrganization>()
                .eq(SysOrganization::getParentOrgId, parentId)
                .eq(SysOrganization::getDeleted, 0));

        for (SysOrganization child : children) {
            String newPath = parentPath + "/" + child.getOrgId();
            int newLevel = parentLevel + 1;
            child.setOrgPath(newPath);
            child.setOrgLevel(newLevel);
            updateById(child);

            // 递归更新子组织
            updateChildrenOrgPath(child.getOrgId(), newPath, newLevel);
        }
    }
}
