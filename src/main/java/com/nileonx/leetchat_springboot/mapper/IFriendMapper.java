package com.nileonx.leetchat_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nileonx.leetchat_springboot.entities.FriendEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IFriendMapper extends BaseMapper<FriendEntity> {
    @Select("select * from friend where fromid = #{fromUid} and toid = #{toUid}")
    FriendEntity selectByFromUidAndToUid(@Param("fromUid") Long fromUid,@Param("toUid") Long toUid);
    @Select("select toid from friend where fromid = #{fromUid}")
    List<Long> getFriendIdList(@Param("fromUid") Long fromUid);
}
