package com.nileonx.leetchat_springboot.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("friend")
@Data
public class FriendEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long fromid;
    private Long toid;
    private Integer status;
}
