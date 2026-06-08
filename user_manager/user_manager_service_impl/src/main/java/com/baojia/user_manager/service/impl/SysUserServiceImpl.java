package com.baojia.user_manager.service.impl;

import com.baojia.platform.common.PageResultVo;
import com.baojia.user_manager.mapper.SysUserMapper;
import com.baojia.user_manager.model.SysOrganization;
import com.baojia.user_manager.model.SysRole;
import com.baojia.user_manager.model.SysUser;
import com.baojia.user_manager.model.SysUserRole;
import com.baojia.user_manager_adapter.dto.UserOrgNodeQueryDto;
import com.baojia.user_manager_adapter.dto.UserRoleOrgPageQueryDto;
import com.baojia.user_manager_adapter.vo.UserQueryItemVo;
import com.baojia.user_manager.service.ISysOrganizationService;
import com.baojia.user_manager.service.ISysRoleService;
import com.baojia.user_manager.service.ISysUserRoleService;
import com.baojia.user_manager.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liudeqing
 * @date 2025/8/29
 * @description
 */
@SuppressWarnings("all")
@Service
@Slf4j
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final ISysOrganizationService organizationService;

    private final ISysRoleService roleService;

    private final ISysUserRoleService userRoleService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser createUser(SysUser user) {
        // 检查用户名是否存在
        if (checkUsernameExists(user.getUsername(), null)) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否存在
        if (StringUtils.hasText(user.getPhone()) && checkPhoneExists(user.getPhone(), null)) {
            throw new RuntimeException("手机号已存在");
        }

        // 检查邮箱是否存在
        if (StringUtils.hasText(user.getEmail()) && checkEmailExists(user.getEmail(), null)) {
            throw new RuntimeException("邮箱已存在");
        }

        // 检查组织是否存在
        if (user.getOrgId() != null) {
            SysOrganization org = organizationService.getById(user.getOrgId());
            if (org == null) {
                throw new RuntimeException("组织不存在");
            }
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setStatus(1);
        user.setUserType(1); // 默认普通用户
        save(user);

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser updateUser(SysUser user) {
        SysUser existingUser = getById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户名是否存在
        if (StringUtils.hasText(user.getUsername())
                && !user.getUsername().equals(existingUser.getUsername())) {
            if (checkUsernameExists(user.getUsername(), user.getUserId())) {
                throw new RuntimeException("用户名已存在");
            }
        }

        // 检查手机号是否存在
        if (StringUtils.hasText(user.getPhone())
                && !user.getPhone().equals(existingUser.getPhone())) {
            if (checkPhoneExists(user.getPhone(), user.getUserId())) {
                throw new RuntimeException("手机号已存在");
            }
        }

        // 检查邮箱是否存在
        if (StringUtils.hasText(user.getEmail())
                && !user.getEmail().equals(existingUser.getEmail())) {
            if (checkEmailExists(user.getEmail(), user.getUserId())) {
                throw new RuntimeException("邮箱已存在");
            }
        }

        updateById(user);
        return getById(user.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        return removeById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUsers(List<Long> userIds) {
        return removeByIds(userIds);
    }

    @Override
    public SysUser getUserById(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public SysUser login(String username, String password) {
        SysUser user = baseMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setPasswordUpdateTime(LocalDateTime.now());

        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        user.setPasswordUpdateTime(LocalDateTime.now());

        return updateById(user);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        return updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色
        userRoleService.removeByUserId(userId);

        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            SysUser user = getById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            List<SysUserRole> userRoles = roleIds.stream().map(roleId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setOrgId(user.getOrgId());
                return userRole;
            }).collect(Collectors.toList());

            userRoleService.saveBatch(userRoles);
        }

        return true;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        return userRoleService.selectRoleIdsByUserId(userId);
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user);

        // 获取组织信息
        if (user.getOrgId() != null) {
            SysOrganization org = organizationService.getById(user.getOrgId());
            userInfo.put("organization", org);
        }

        // 获取角色信息
        List<SysRole> roles = roleService.getRolesByUserId(userId);
        userInfo.put("roles", roles);

        return userInfo;
    }

    @Override
    public boolean checkUsernameExists(String username, Long excludeUserId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0);
        if (excludeUserId != null) {
            wrapper.ne(SysUser::getUserId, excludeUserId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public boolean checkPhoneExists(String phone, Long excludeUserId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone)
                .eq(SysUser::getDeleted, 0);
        if (excludeUserId != null) {
            wrapper.ne(SysUser::getUserId, excludeUserId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public boolean checkEmailExists(String email, Long excludeUserId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email)
                .eq(SysUser::getDeleted, 0);
        if (excludeUserId != null) {
            wrapper.ne(SysUser::getUserId, excludeUserId);
        }
        return count(wrapper) > 0;
    }

    @Override
    public List<SysUser> getUsersByOrgId(Long orgId) {
        return baseMapper.selectUsersByOrgId(orgId);
    }

    @Override
    public boolean updateLoginInfo(Long userId, String loginIp) {
        int result = baseMapper.updateLoginInfo(userId, LocalDateTime.now(), loginIp);
        return result > 0;
    }

    @Override
    public PageResultVo<UserQueryItemVo> pageQueryByRoleAndOrg(UserRoleOrgPageQueryDto queryDto) {
        Integer pageNum = queryDto.getPageNum() == null || queryDto.getPageNum() < 1 ? 1 : queryDto.getPageNum();
        Integer pageSize = queryDto.getPageSize() == null || queryDto.getPageSize() < 1 ? 10 : queryDto.getPageSize();
        long offset = (long) (pageNum - 1) * pageSize;

        List<Long> orgIds = null;
        if (queryDto.getOrgId() != null) {
            orgIds = organizationService.getOrgAndChildrenIds(queryDto.getOrgId());
        }

        List<SysUser> users = baseMapper.selectUsersByRoleAndOrgPage(queryDto.getRoleId(), orgIds, offset, Long.valueOf(pageSize));
        Long total = baseMapper.countUsersByRoleAndOrg(queryDto.getRoleId(), orgIds);

        PageResultVo<UserQueryItemVo> resultVo = new PageResultVo<>();
        resultVo.setPageNum(pageNum);
        resultVo.setPageSize(pageSize);
        resultVo.setTotal(total == null ? 0L : total);
        resultVo.setRecords(buildUserQueryItems(users));
        return resultVo;
    }

    @Override
    public PageResultVo<UserQueryItemVo> pageQueryByOrgNode(UserOrgNodeQueryDto queryDto) {
        if (queryDto.getOrgId() == null) {
            throw new RuntimeException("组织节点ID不能为空");
        }
        UserRoleOrgPageQueryDto inner = new UserRoleOrgPageQueryDto();
        inner.setOrgId(queryDto.getOrgId());
        inner.setPageNum(queryDto.getPageNum());
        inner.setPageSize(queryDto.getPageSize());
        return pageQueryByRoleAndOrg(inner);
    }

    private List<UserQueryItemVo> buildUserQueryItems(List<SysUser> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> orgIds = users.stream().map(SysUser::getOrgId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> orgNameMap = new HashMap<>();
        if (!orgIds.isEmpty()) {
            List<SysOrganization> orgs = organizationService.listByIds(orgIds);
            for (SysOrganization org : orgs) {
                orgNameMap.put(org.getOrgId(), org.getOrgName());
            }
        }

        List<UserQueryItemVo> records = new ArrayList<>();
        for (SysUser user : users) {
            UserQueryItemVo vo = new UserQueryItemVo();
            vo.setUserId(user.getUserId());
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
            vo.setOrgId(user.getOrgId());
            vo.setOrgName(orgNameMap.get(user.getOrgId()));
            vo.setRoleIds(userRoleService.selectRoleIdsByUserId(user.getUserId()));
            records.add(vo);
        }
        return records;
    }
}
