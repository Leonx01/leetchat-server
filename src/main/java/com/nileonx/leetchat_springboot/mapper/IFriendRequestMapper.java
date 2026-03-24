package com.nileonx.leetchat_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nileonx.leetchat_springboot.entities.FriendRequestEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IFriendRequestMapper extends BaseMapper<FriendRequestEntity>   {
    @Select("select * from friendrequest where fromid = #{fromUid}")
    List<FriendRequestEntity> selectByFromUid(@Param("fromUid") Long fromUid);
    @Select("select * from friendrequest where toid = #{toUid}")
    List<FriendRequestEntity> selectByToUid(@Param("toUid") Long toUid);
    @Select("select * from friendrequest where fromid = #{fromUid} and toid = #{toUid}")
    FriendRequestEntity selectByFromUidAndToUid(@Param("fromUid") Long fromUid, @Param("toUid") Long toUid);

}
