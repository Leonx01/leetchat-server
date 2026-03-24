package com.nileonx.leetchat_springboot.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@TableName(value = "file")
@Data
public class FileEntity {
    @TableId(type = IdType.AUTO)
    private Long fid;
    private String fname;
    private String type;
    private Timestamp expiretime;
    private Timestamp uploadtime;
    private String osskey;
    private Long uid;
}
