package com.nileonx.leetchat_springboot.vo;

import lombok.Data;

@Data
public class MsgVO {
    private Long mid;
    private SimpleUserVO from;
    private SimpleUserVO to;
    private MsgMetaVO toMessage;
    private MsgMetaVO fromMessage;

}
