package com.nileonx.leetchat_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nileonx.leetchat_springboot.entities.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface IUserMapper extends BaseMapper<UserEntity> {
    //继承了BaseMapper,有预先写好的Mapper函数
    @Update("update user set avatar = #{avatar} where uname = #{uname}")
    void updateAvatar(@Param("uname") String uname, @Param("avatar") String avatar);
    @Select("select * from user where uname = #{uname}")
    UserEntity selectByUsername( String uname);

    @Select("select * from user where uid = #{uid}")
    UserEntity selectByUid(Long uid);
    @Select("select uid from user where uname = #{uname}")
    Long gerUserId(String uname);
    @Update("update user set online = #{status} where uid = #{uid}")
    void setOnlineStatus(@Param("uid") String uid, @Param("status") int status);
    @Update("update user set unick = #{unick}, selfinfo = #{selfInfo} where uname = #{uname}")
    void updateUnickAndSelfInfo(@Param("uname") String uname, @Param("unick") String unick, @Param("selfInfo") String selfInfo);
    //reset password
    @Update("update user set upass = #{password} where uname = #{uname}")
    void resetPassword(@Param("uname") String uname, @Param("password") String password);
}