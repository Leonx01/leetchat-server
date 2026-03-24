package com.nileonx.leetchat_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nileonx.leetchat_springboot.entities.PrivateMsgEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IPrivateMsgMapper extends BaseMapper<PrivateMsgEntity> {
    //select by wid 20条
//    @Select("select * from privatemessage where sid = #{sid} order by sendtime desc limit 20 ")
    @Select("select * from privatemessage where sid = #{sid} order by sendtime desc")
    public List< PrivateMsgEntity> getMsgBySid(Long sid);

    @Select("SELECT COALESCE(MAX(mid), 0) FROM privatemessage WHERE sid = #{sid}")
    Long getMaxMidBySid(@Param("sid") Long sid);
}
