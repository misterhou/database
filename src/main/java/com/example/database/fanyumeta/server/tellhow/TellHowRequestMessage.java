package com.example.database.fanyumeta.server.tellhow;

import com.example.database.fanyumeta.server.ServiceType;
import lombok.Data;

/**
 * 接收到泰豪的消息
 */
@Data
public class TellHowRequestMessage {

    /**
     * 请求 id，唯一标识
     */
    private String id;

    /**
     * 请求服务
     */
    private ServiceType serviceType;

    /**
     * 请求数据
     */
    private Object data;

}
