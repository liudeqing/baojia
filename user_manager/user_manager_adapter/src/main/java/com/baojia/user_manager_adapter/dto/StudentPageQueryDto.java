package com.baojia.user_manager_adapter.dto;

import lombok.Data;

/**
 * 分页查询学生；支持按姓名、监护人手机号模糊筛选。
 */
@Data
public class StudentPageQueryDto {

    private Integer pageNum;

    private Integer pageSize;

    /** 学生姓名（模糊） */
    private String studentName;

    /** 学生监护人手机号（模糊） */
    private String studentLinkPhone;
}
