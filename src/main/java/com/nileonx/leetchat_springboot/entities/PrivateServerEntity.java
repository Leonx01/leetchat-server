package com.nileonx.leetchat_springboot.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName(value = "privateserver")
public class PrivateServerEntity {
    @TableId(type = IdType.AUTO)
    private Long sid;
    private Timestamp createtime;
    private Integer status;

}
