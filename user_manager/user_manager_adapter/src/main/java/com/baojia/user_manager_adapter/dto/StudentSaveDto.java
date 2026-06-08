package com.baojia.user_manager_adapter.dto;

import lombok.Data;

/**
 * 新增或修改学生。
 */
@Data
public class StudentSaveDto {

    /** 修改时传入 */
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
}
