package com.baojia.user_manager.mapper;

import com.baojia.user_manager.model.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author liudeqing
 * @date 2025/8/29
 * @description
 */
@SuppressWarnings( "all" )
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND deleted = 0")
    SysUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email} AND deleted = 0")
    SysUser selectByEmail(@Param("email") String email);

    /**
     * 更新用户最后登录信息
     */
    @Update("UPDATE sys_user SET last_login_time = #{loginTime}, last_login_ip = #{loginIp} WHERE user_id = #{userId}")
    int updateLoginInfo(@Param("userId") Long userId,
                        @Param("loginTime") java.time.LocalDateTime loginTime,
                        @Param("loginIp") String loginIp);

    /**
     * 查询组织下的用户列表
     */
    @Select("SELECT * FROM sys_user WHERE org_id = #{orgId} AND deleted = 0 ORDER BY sort_order, create_time")
    List<SysUser> selectUsersByOrgId(@Param("orgId") Long orgId);

    @Select({
            "<script>",
            "SELECT DISTINCT u.*",
            "FROM sys_user u",
            "LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id",
            "WHERE u.deleted = 0",
            "<if test='roleId != null'>",
            "  AND ur.role_id = #{roleId}",
            "</if>",
            "<if test='orgIds != null and orgIds.size() > 0'>",
            "  AND u.org_id IN",
            "  <foreach collection='orgIds' item='orgId' open='(' separator=',' close=')'>",
            "    #{orgId}",
            "  </foreach>",
            "</if>",
            "ORDER BY u.sort_order, u.create_time DESC",
            "LIMIT #{offset}, #{size}",
            "</script>"
    })
    List<SysUser> selectUsersByRoleAndOrgPage(@Param("roleId") Long roleId,
                                              @Param("orgIds") List<Long> orgIds,
                                              @Param("offset") Long offset,
                                              @Param("size") Long size);

    @Select({
            "<script>",
            "SELECT COUNT(DISTINCT u.user_id)",
            "FROM sys_user u",
            "LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id",
            "WHERE u.deleted = 0",
            "<if test='roleId != null'>",
            "  AND ur.role_id = #{roleId}",
            "</if>",
            "<if test='orgIds != null and orgIds.size() > 0'>",
            "  AND u.org_id IN",
            "  <foreach collection='orgIds' item='orgId' open='(' separator=',' close=')'>",
            "    #{orgId}",
            "  </foreach>",
            "</if>",
            "</script>"
    })
    Long countUsersByRoleAndOrg(@Param("roleId") Long roleId,
                                @Param("orgIds") List<Long> orgIds);

}
