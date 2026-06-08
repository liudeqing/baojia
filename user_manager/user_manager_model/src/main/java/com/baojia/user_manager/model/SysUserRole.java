package com.baojia.user_manager.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("sys_user_role")
public class SysUserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long roleId;

    private Long orgId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
