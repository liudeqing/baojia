package com.baojia.user_manager.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生管理；{@link #status}：0 正常，1 已删除（逻辑删除）。
 */
@Data
@TableName("student")
public class Student {

    /** 正常（可查可改） */
    public static final int STATUS_NORMAL = 0;
    /** 逻辑删除 */
    public static final int STATUS_DELETED = 1;

    @TableId(type = IdType.AUTO)
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

    private Integer status;

    private Long createBy;

    private Long updateBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
