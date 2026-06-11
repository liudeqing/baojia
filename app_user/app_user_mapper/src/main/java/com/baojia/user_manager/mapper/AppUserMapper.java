package com.baojia.user_manager.mapper;

import com.baojia.user_manager.model.AppUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@SuppressWarnings( "all" )
public interface AppUserMapper extends BaseMapper<AppUser> {

    /**
     * 删除用户的所有角色
     */
    @Delete("DELETE FROM app_user WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);
}
