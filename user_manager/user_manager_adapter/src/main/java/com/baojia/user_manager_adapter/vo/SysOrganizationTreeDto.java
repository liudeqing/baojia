package com.baojia.user_manager_adapter.vo;

import lombok.Data;

import java.util.List;

@Data
public class SysOrganizationTreeDto {

    private Long orgId;

    private String orgName;

    private String orgCode;

    private Integer orgType;

    private Long parentOrgId;

    private Integer orgLevel;

    private String orgPath;

    private Integer status;

    private String remark;

    private List<SysOrganizationTreeDto> children;

}
