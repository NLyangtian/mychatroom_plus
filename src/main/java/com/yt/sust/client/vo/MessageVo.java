package com.yt.sust.client.vo;

import lombok.Data;

/**
 * 服务器与客户端传递信息的载体
 */
@Data
public class MessageVo {
    /**
     * 表示告知服务器要进行的动作，1表示用户注册，2表示私聊
     */
    private String type;
    /**
     * 发送到服务器的具体内容
     *
     */
    private String content;
    /**
     * 私聊告知服务器要将哪些信息发送给哪个用户
     */
    private  String to;
}
