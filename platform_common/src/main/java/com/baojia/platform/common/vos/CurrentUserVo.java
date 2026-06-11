package com.baojia.platform.common.vos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@SuppressWarnings("all")
public class CurrentUserVo {

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 登录名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * email
     */
    private String email;

    /**
     * 角色列表,在App侧不用
     */
    private List<String> roleCodes;

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
