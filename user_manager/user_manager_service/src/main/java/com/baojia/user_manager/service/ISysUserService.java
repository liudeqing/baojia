package com.baojia.user_manager.service;
import com.baojia.platform.common.PageResultVo;
import com.baojia.user_manager.model.SysUser;
import com.baojia.user_manager_adapter.dto.UserOrgNodeQueryDto;
import com.baojia.user_manager_adapter.dto.UserRoleOrgPageQueryDto;
import com.baojia.user_manager_adapter.vo.UserQueryItemVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author liudeqing
 * @date 2025/8/29
 * @description
 */
@SuppressWarnings( "all" )
public interface ISysUserService extends IService<SysUser> {

    /**
     * 创建用户
     */
    SysUser createUser(SysUser user);

    /**
     * 更新用户
     */
    SysUser updateUser(SysUser user);

    /**
     * 删除用户（逻辑删除）
     */
    boolean deleteUser(Long userId);

    /**
     * 批量删除用户
     */
    boolean batchDeleteUsers(List<Long> userIds);

    /**
     * 获取用户详情
     */
    SysUser getUserById(Long userId);

    /**
     * 根据用户名获取用户
     */
    SysUser getUserByUsername(String username);

    /**
     * 用户登录
     */
    SysUser login(String username, String password);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 更新用户状态
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 分配用户角色
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 获取用户信息（包含组织、角色）
     */
    Map<String, Object> getUserInfo(Long userId);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username, Long excludeUserId);

    /**
     * 检查手机号是否存在
     */
    boolean checkPhoneExists(String phone, Long excludeUserId);

    /**
     * 检查邮箱是否存在
     */
    boolean checkEmailExists(String email, Long excludeUserId);

    /**
     * 获取组织下的用户列表
     */
    List<SysUser> getUsersByOrgId(Long orgId);

    /**
     * 更新最后登录信息
     */
    boolean updateLoginInfo(Long userId, String loginIp);

    /**
     * 按角色和组织分页查询用户
     */
    PageResultVo<UserQueryItemVo> pageQueryByRoleAndOrg(UserRoleOrgPageQueryDto queryDto);

    /**
     * 查询组织节点及其所有下级节点用户（分页）
     */
    PageResultVo<UserQueryItemVo> pageQueryByOrgNode(UserOrgNodeQueryDto queryDto);
}
