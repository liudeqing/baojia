package com.baojia.user_manager.service;

import com.baojia.user_manager.model.SysOrganization;
import com.baojia.user_manager_adapter.vo.SysOrganizationTreeDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ISysOrganizationService extends IService<SysOrganization> {

    /**
     * 创建组织
     */
    SysOrganization createOrganization(SysOrganization organization);

    /**
     * 更新组织
     */
    SysOrganization updateOrganization(SysOrganization organization);

    /**
     * 删除组织（逻辑删除，同时检查是否有子组织）
     */
    boolean deleteOrganization(Long orgId,Long operatorUserId);

    /**
     * 获取组织详情
     */
    SysOrganization getOrganizationById(Long orgId);

    /**
     * 获取组织树
     */
    List<SysOrganizationTreeDto> getOrganizationTree();

    /**
     * 获取组织及所有子组织ID
     */
    List<Long> getOrgAndChildrenIds(Long orgId);

    /**
     * 移动组织（改变父组织）
     */
    boolean moveOrganization(Long orgId, Long newParentId);

    /**
     * 启用/禁用组织
     */
    boolean updateOrganizationStatus(Long orgId, Integer status);

    /**
     * 检查组织编码是否存在
     */
    boolean checkOrgCodeExists(String orgCode, Long excludeOrgId);

    /**
     * 获取组织下的所有用户数量
     */
    int countUsersByOrgId(Long orgId);
}
