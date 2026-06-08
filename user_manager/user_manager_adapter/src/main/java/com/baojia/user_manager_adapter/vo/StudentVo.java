package com.baojia.user_manager_adapter.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentVo {

    private Long studentId;

    private String studentName;

    private String studentSchoolName;

    private String studentGradeName;

    private String studentClassName;

    private String studentSexName;

    private String studentBirthday;

    private String studentLinkName;

    private String studentLinkPhone;

    private String studentLinkType;

    private String studentParentName;

    /** 0 正常 1 已删除 */
    private Integer status;

    private String statusLabel;

    private Long createBy;

    private Long updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
