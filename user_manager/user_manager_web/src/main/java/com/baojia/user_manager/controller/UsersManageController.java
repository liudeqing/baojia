package com.baojia.user_manager.controller;

import com.baojia.platform.common.PageResultVo;
import com.baojia.platform.common.ResultModuleVo;
import com.baojia.user_manager.model.SysUser;
import com.baojia.user_manager.security.CurrentUserDetail;
import com.baojia.user_manager.service.ISysUserService;
import com.baojia.user_manager_adapter.dto.CreateUserRequestDto;
import com.baojia.user_manager_adapter.dto.UserOrgNodeQueryDto;
import com.baojia.user_manager_adapter.dto.UserRoleOrgPageQueryDto;
import com.baojia.user_manager_adapter.vo.CreateUserResultVo;
import com.baojia.user_manager_adapter.vo.UserQueryItemVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author liudeqing
 * @date 2025/8/29
 * @description
 */
@SuppressWarnings( "all" )
@RestController
@RequestMapping( "/body")
@Slf4j
public class UsersManageController {

    @Resource
    private ISysUserService sysUserService;

    /*@RequestMapping( value = "/list" , method = RequestMethod.GET )
    public List<UserResultDto> getBodyList(@RequestParam("name") String name){
        return sysUserService.get( name,"");
    }*/

    @RequestMapping( value = "/create" , method = RequestMethod.GET )
    public ResultModuleVo<Boolean> createUser(@RequestParam(value = "count", defaultValue = "1") int count ){
        log.info("createUser stub count={}", count );
        SysUser sysUser = new SysUser();
        return ResultModuleVo.success( sysUserService.saveBatch( Arrays.asList( sysUser ) ) );
    }

    /**
     * 按角色+组织分页查询（组织支持查询当前节点及下级节点）
     */
    @PostMapping("/user/page")
    public ResultModuleVo<PageResultVo<UserQueryItemVo>> pageQueryByRoleAndOrg(@RequestBody UserRoleOrgPageQueryDto queryDto) {
        return ResultModuleVo.success( sysUserService.pageQueryByRoleAndOrg(queryDto));
    }

    /**
     * 创建用户（明文密码由服务端加密）；可选分配角色。
     */
    @PostMapping("/user/create")
    public ResultModuleVo<CreateUserResultVo> createUser(@RequestBody CreateUserRequestDto dto) {
        log.info("创建用户 username={}", dto != null ? dto.getUsername() : null);
        if (dto == null || !StringUtils.hasText(dto.getUsername())) {
            throw new RuntimeException("用户名不能为空");
        }
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new RuntimeException("密码不能为空");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(dto.getPassword());
        user.setRealName(StringUtils.hasText(dto.getRealName()) ? dto.getRealName().trim() : null);
        user.setPhone(StringUtils.hasText(dto.getPhone()) ? dto.getPhone().trim() : null);
        user.setEmail(StringUtils.hasText(dto.getEmail()) ? dto.getEmail().trim() : null);
        user.setOrgId(dto.getOrgId());
        user.setCreateTime(LocalDateTime.now() );
        user.setUpdateTime(LocalDateTime.now() );
        user.setCreateBy(CurrentUserDetail.userIdOrNull());
        user.setUpdateBy(CurrentUserDetail.userIdOrNull());
        SysUser created = sysUserService.createUser(user);
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            sysUserService.assignRoles(created.getUserId(), dto.getRoleIds());
        }
        CreateUserResultVo vo = new CreateUserResultVo();
        vo.setUserId(created.getUserId());
        vo.setUsername(created.getUsername());
        return ResultModuleVo.success( vo );
    }

    /**
     * 查询组织节点及其下级节点用户（分页）
     */
    @PostMapping("/user/page/by-org-node")
    public ResultModuleVo<PageResultVo<UserQueryItemVo>> pageQueryByOrgNode(@RequestBody UserOrgNodeQueryDto queryDto) {
        return ResultModuleVo.success( sysUserService.pageQueryByOrgNode(queryDto));
    }

}
