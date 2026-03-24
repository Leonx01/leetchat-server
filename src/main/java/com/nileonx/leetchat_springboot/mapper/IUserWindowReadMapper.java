package com.nileonx.leetchat_springboot.mapper;

import org.apache.ibatis.annotations.*;

/**
 * 用户窗口已读位置，用于计算未读数
 */
public interface IUserWindowReadMapper {

    /**
     * 插入或更新已读位置
     */
    @Insert("INSERT INTO user_window_read (uid, wid, last_read_mid) VALUES (#{uid}, #{wid}, #{lastReadMid}) " +
            "ON DUPLICATE KEY UPDATE last_read_mid = VALUES(last_read_mid)")
    void upsert(@Param("uid") Long uid, @Param("wid") Long wid, @Param("lastReadMid") Long lastReadMid);

    /**
     * 统计某用户在某窗口的未读消息数（该窗口下 toid=uid 且 mid > last_read_mid 的消息数）
     */
    @Select("SELECT COUNT(*) FROM privatemessage p " +
            "LEFT JOIN user_window_read r ON r.uid = #{uid} AND r.wid = #{wid} " +
            "WHERE p.sid = #{sid} AND p.toid = #{uid} " +
            "AND (r.last_read_mid IS NULL OR p.mid > r.last_read_mid)")
    int countUnread(@Param("uid") Long uid, @Param("wid") Long wid, @Param("sid") Long sid);
}
