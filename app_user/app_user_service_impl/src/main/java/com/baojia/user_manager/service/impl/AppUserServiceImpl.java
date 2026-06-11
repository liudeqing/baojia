package com.baojia.user_manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.baojia.platform.common.dtos.AppUserLoginRequestDto;
import com.baojia.user_manager.mapper.AppUserMapper;
import com.baojia.user_manager.model.AppUser;
import com.baojia.user_manager.service.IAppUserService;
import com.baojia.user_manager_adapter.dto.RegisterAppUserDto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements IAppUserService {

    private final AppUserMapper appUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public AppUser register(RegisterAppUserDto user) {

        // 检查手机号是否存在
        if (StringUtils.hasText(user.getPhone()) && checkPhoneExists( user.getPhone() ) ) {
            throw new RuntimeException("手机号已存在");
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        AppUser appUser = new AppUser();
        appUser.setPhone(user.getPhone());
        appUser.setPassword(encodedPassword);
        appUser.setNickname( user.getUsername() );
        appUser.setUsername( user.getUsername() );
        appUser.setCreateTime( LocalDateTime.now() );
        appUser.setUpdateTime( LocalDateTime.now() );
        appUser.setRealName( "" );
        appUser.setCreateBy( 0L );
        appUser.setUpdateBy( 0L );
        save(appUser);

        return appUser;
    }

    @Override
    @Transactional
    public boolean checkPhoneExists(String phone) {
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppUser::getPhone, phone);
        return count(wrapper) > 0;
    }

    @Override
    @Transactional
    public boolean checkEmailExists(String email) {
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppUser::getEmail, email);
        return count(wrapper) > 0;
    }

    @Override
    public AppUser login(AppUserLoginRequestDto req) {
        log.info( "login,req:{}" , JSON.toJSONString(req) );
        LambdaQueryWrapper<AppUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppUser::getPassword, req.getPassword() ).eq( AppUser::getUsername , req.getUsername() );
        return appUserMapper.selectOne( wrapper );
    }
}
