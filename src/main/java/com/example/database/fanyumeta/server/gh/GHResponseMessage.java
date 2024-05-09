package com.example.database.fanyumeta.server.gh;

import com.example.database.fanyumeta.server.Service;
import com.example.database.fanyumeta.server.tellhow.TellHowRequestMessage;
import com.example.database.fanyumeta.utils.StringUtils;
import lombok.Data;

/**
 * 发送给广哈的消息
 */
@Data
public class GHResponseMessage {

    public GHResponseMessage(String tellHowReqId, Service service, Object data) {
        this(service, data);
        this.tellHowReqId = tellHowReqId;
    }

    public GHResponseMessage(Service service, Object data) {
        this.id = StringUtils.getUUID();
        this.service = service;
        this.data = data;
    }

    public GHResponseMessage(TellHowRequestMessage tellHowRequestMessage) {
        this(tellHowRequestMessage.getId(),
                tellHowRequestMessage.getService(),
                tellHowRequestMessage.getData());
    }

    /**
     * 请求 id，唯一标识
     */
    private String id;

    /**
     * 泰豪请求 id
     */
    private String tellHowReqId;

    /**
     * 请求服务
     */
    private Service service;

    /**
     * 请求数据
     */
    private Object data;

}
