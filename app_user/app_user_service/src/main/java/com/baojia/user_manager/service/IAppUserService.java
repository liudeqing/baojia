package com.baojia.user_manager.service;
import com.baojia.platform.common.PageResultVo;
import com.baojia.platform.common.dtos.AppUserLoginRequestDto;
import com.baojia.user_manager.model.AppUser;
import com.baojia.user_manager_adapter.dto.RegisterAppUserDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @author liudeqing
 * @date 2025/8/29
 * @description
 */
@SuppressWarnings( "all" )
public interface IAppUserService extends IService<AppUser> {

    /**
     * 注册
     */
    AppUser register(RegisterAppUserDto user);

    /**
     * 检测手机号是否已存在
     * @param phone
     * @return
     */
    boolean checkPhoneExists(String phone);

    /**
     * 检测Email是否存在
     * @param email
     * @return
     */
    boolean checkEmailExists(String email);

    /**
     * 用户登陆
     * @param req
     * @return
     */
    AppUser login( AppUserLoginRequestDto req);

}
