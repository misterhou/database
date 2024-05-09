package com.example.database.fanyumeta.server.tellhow;

import com.example.database.fanyumeta.server.Service;
import lombok.Data;

/**
 * 发送给泰豪的消息
 */
@Data
public class TellHowRequestMessage {

    /**
     * 请求 id，唯一标识
     */
    private String id;

    /**
     * 广哈请求 id
     */
    private String ghReqId;

    /**
     * 请求服务
     */
    private Service service;

    /**
     * 请求数据
     */
    private Object data;

}
