package com.baojia.user_manager.mapper;

import com.baojia.user_manager.model.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@SuppressWarnings( "all" )
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色编码查询角色
     */
    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} AND deleted = 0")
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 查询用户拥有的角色
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询组织下的角色列表
     */
    @Select("SELECT * FROM sys_role WHERE org_id = #{orgId} AND deleted = 0 ORDER BY create_time")
    List<SysRole> selectRolesByOrgId(@Param("orgId") Long orgId);
}
