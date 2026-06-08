package com.baojia.user_manager.controller;

import com.alibaba.fastjson2.JSON;
import com.baojia.platform.common.ResultModuleVo;
import com.baojia.user_manager.model.SysOrganization;
import com.baojia.user_manager.security.CurrentUserDetail;
import com.baojia.user_manager.service.ISysOrganizationService;
import com.baojia.user_manager_adapter.dto.AddSysOrganizationDto;
import com.baojia.user_manager_adapter.dto.UpdateSysOrganizationDto;
import com.baojia.user_manager_adapter.vo.SysOrganizationTreeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings( "all" )
@RestController
@RequestMapping( "/organization")
@Slf4j
public class OrganizationController {

    @Autowired
    private ISysOrganizationService sysOrganizationService;

    @RequestMapping( value = "/create" , method = RequestMethod.POST )
    public ResultModuleVo<SysOrganization> createOrganization(@RequestBody AddSysOrganizationDto addSysOrganizationDto ){
        log.info("添加组织机构,数据信息为:{}", JSON.toJSONString( addSysOrganizationDto ) );
        try {
            SysOrganization sysOrganization = new SysOrganization();
            BeanUtils.copyProperties( addSysOrganizationDto, sysOrganization );
            sysOrganization.setCreateTime( LocalDateTime.now() );
            sysOrganization.setUpdateTime( LocalDateTime.now() );
            return ResultModuleVo.success( sysOrganizationService.createOrganization( sysOrganization ) );
        } catch (RuntimeException e) {
            log.warn("添加组织机构失败: {}", e.getMessage());
            return ResultModuleVo.failure(400, e.getMessage() == null ? "创建失败" : e.getMessage());
        }
    }

    /** 树形数据只读，使用 GET，与前端、网关约定路径一致：GET /organization/tree */
    @GetMapping("/tree")
    public ResultModuleVo<List<SysOrganizationTreeDto>> getOrganizationTree() {
        log.info("获取组织机构树形数据");
        return ResultModuleVo.success( sysOrganizationService.getOrganizationTree() );
    }

    @PostMapping("/update")
    public ResultModuleVo<SysOrganization> updateOrganization(@RequestBody UpdateSysOrganizationDto dto) throws Exception {
        log.info("更新组织机构,数据信息为:{}", JSON.toJSONString(dto));
        try {
            if (dto.getOrgId() == null) {
                return ResultModuleVo.failure(400, "组织ID不能为空");
            }
            SysOrganization existing = sysOrganizationService.getOrganizationById(dto.getOrgId());
            if (dto.getOrgName() != null && !dto.getOrgName().isBlank()) {
                existing.setOrgName(dto.getOrgName().trim());
            }
            if (dto.getOrgCode() != null && !dto.getOrgCode().isBlank()) {
                existing.setOrgCode(dto.getOrgCode().trim());
            }
            if (dto.getOrgType() != null) {
                existing.setOrgType(dto.getOrgType());
            }
            if (dto.getStatus() != null) {
                existing.setStatus(dto.getStatus());
            }
            if (dto.getRemark() != null) {
                existing.setRemark(dto.getRemark());
            }
            if (dto.getSortOrder() != null) {
                existing.setSortOrder(dto.getSortOrder());
            }
            return ResultModuleVo.success(sysOrganizationService.updateOrganization(existing));
        } catch (RuntimeException e) {
            log.warn("更新组织机构失败: {}", e.getMessage());
            return ResultModuleVo.failure(400, e.getMessage() == null ? "更新失败" : e.getMessage());
        }
    }

    @DeleteMapping("/{orgId}")
    public ResultModuleVo<Boolean> deleteOrganization(@PathVariable(name = "orgId") Long orgId) {
        log.info("删除组织机构 orgId={}", orgId);
        try {
            return ResultModuleVo.success(sysOrganizationService.deleteOrganization(orgId, CurrentUserDetail.userIdOrNull()));
        } catch (RuntimeException e) {
            log.warn("删除组织机构失败: {}", e.getMessage());
            return ResultModuleVo.failure(400, e.getMessage() == null ? "删除失败" : e.getMessage());
        }
    }

}
