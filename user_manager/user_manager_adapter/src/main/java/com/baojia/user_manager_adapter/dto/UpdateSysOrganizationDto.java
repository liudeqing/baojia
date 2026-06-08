package com.baojia.user_manager_adapter.dto;

import lombok.Data;

@Data
public class UpdateSysOrganizationDto {

    private Long orgId;

    private String orgName;

    private String orgCode;

    private Integer orgType;

    private Integer status;

    private String remark;

    private Integer sortOrder;
}
