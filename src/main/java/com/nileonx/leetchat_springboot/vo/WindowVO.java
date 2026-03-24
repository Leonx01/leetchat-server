package com.nileonx.leetchat_springboot.vo;
//import
import lombok.Data;
//lombok.Data;

import java.sql.Timestamp;
@Data
public class WindowVO {
    private SimpleUserVO user;
    private Long wid;
    private Long sid;
    private Timestamp lastactivetime;
    /** 该窗口下当前用户的未读消息数 */
    private Integer unreadCount;
}
