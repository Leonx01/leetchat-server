package com.nileonx.leetchat_springboot.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;

@Data
@TableName(value = "privatewindow")
public class WindowEntity {
    @TableId(type = IdType.AUTO)
    private Long wid;
    private Long fromid;
    private Long toid;
    private Integer active;
    // 0:可见 1:不可见
    private Timestamp lastactivetime;
    private Long sid;
}
