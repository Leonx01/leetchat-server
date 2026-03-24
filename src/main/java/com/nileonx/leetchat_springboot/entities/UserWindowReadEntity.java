package com.nileonx.leetchat_springboot.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户按窗口的已读位置，未读数 = 该窗口下 toid=uid 且 mid > last_read_mid 的消息数
 */
@Data
@TableName(value = "user_window_read")
public class UserWindowReadEntity {
    private Long uid;
    private Long wid;
    private Long lastReadMid;
}
