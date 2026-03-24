package com.nileonx.leetchat_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nileonx.leetchat_springboot.entities.WindowEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IWindowMapper extends BaseMapper<WindowEntity> {
    @Select("select * from privatewindow where fromid=#{fromid} and toid=#{toid}")
    WindowEntity selectWindowByFromidAndToid(@Param("fromid") Long fromid,@Param("toid") Long toid);
    @Select("select * from privatewindow where wid=#{wid}")
    WindowEntity selectWindowByWid(@Param("wid") Long wid);
    @Select("select * from privatewindow where fromid=#{fromid} and active=1")
    List<WindowEntity> getActivateWindows(@Param("fromid") Long fromid);
    //get server id
    @Select("select sid from privatewindow where wid=#{wid}")
    Long getServerIdByWid(@Param("wid") Long wid);
    @Select("select toid from privatewindow where sid=#{sid} and fromid=#{fromid} limit 1")
    Long getToidBySidAndFromid(@Param("sid") Long sid, @Param("fromid") Long fromid);

    @Select("select * from privatewindow where sid=#{sid}")
    List<WindowEntity> selectWindowsBySid(@Param("sid") Long sid);
}
