package com.baojia.user_manager_adapter.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdateAppUserDetail {

    /**
     * 用户编号
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * Email
     */
    private String email;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 出生年月日
     */
    private LocalDate birthday;

    /**
     * 性别
     */
    private Integer gender;

}
