package com.baojia.user_manager.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_organization")
public class SysOrganization {

        @TableId(type = IdType.AUTO)
        private Long orgId;

        private String orgName;

        private String orgCode;

        private Integer orgType;

        private Long parentOrgId;

        private Integer orgLevel;

        private String orgPath;

        private Integer status;

        private Integer sortOrder;

        private String remark;

        @TableField(fill = FieldFill.INSERT)
        private LocalDateTime createTime;

        @TableField(fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updateTime;

        private Long createBy;

        private Long updateBy;

        @TableField(fill = FieldFill.UPDATE)
        private Integer deleted;

}
