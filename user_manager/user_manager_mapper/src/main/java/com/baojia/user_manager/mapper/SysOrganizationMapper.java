package com.baojia.user_manager.mapper;

import com.baojia.user_manager.model.SysOrganization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
@SuppressWarnings( "all" )
public interface SysOrganizationMapper extends BaseMapper<SysOrganization> {

    /**
     * 查询组织及其所有子组织
     */
    List<SysOrganization> selectOrgAndChildren(@Param("orgId") Long orgId);

    /**
     * 更新组织路径
     */
    @Update("UPDATE sys_organization SET org_path = #{orgPath} WHERE org_id = #{orgId}")
    int updateOrgPath(@Param("orgId") Long orgId, @Param("orgPath") String orgPath);

    /**
     * 根据组织编码查询组织
     */
    @Select("SELECT * FROM sys_organization WHERE org_code = #{orgCode} AND deleted = 0")
    SysOrganization selectByOrgCode(@Param("orgCode") String orgCode);

    /**
     * 查询组织下的所有用户数量
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE org_id = #{orgId} AND deleted = 0")
    int countUsersByOrgId(@Param("orgId") Long orgId);

}
