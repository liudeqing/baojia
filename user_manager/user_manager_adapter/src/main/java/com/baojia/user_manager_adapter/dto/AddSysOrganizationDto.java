package com.baojia.user_manager_adapter.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddSysOrganizationDto {

    private String orgName;

    private String orgCode;

    private Integer orgType;

    private Long parentOrgId;

    private Integer orgLevel;

    private String orgPath;

    private String contactPerson;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String industry;

    private Integer employeeCount;

    private Integer status;

    private LocalDateTime expireTime;

    private Integer sortOrder;

    private String remark;

}
