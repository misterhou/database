package com.example.database.fanyumeta.server.gh;

import com.example.database.fanyumeta.server.ServiceType;
import com.example.database.fanyumeta.server.tellhow.TellHowRequestMessage;
import com.example.database.fanyumeta.utils.StringUtils;
import lombok.Data;

/**
 * 发送给广哈的消息
 */
@Data
public class GHResponseMessage {

    public GHResponseMessage(String tellHowReqId, ServiceType serviceType, Object data) {
        this(serviceType, data);
        this.tellHowReqId = tellHowReqId;
    }

    public GHResponseMessage(ServiceType serviceType, Object data) {
        this.id = StringUtils.getUUID();
        this.serviceType = serviceType;
        this.data = data;
    }

    public GHResponseMessage(TellHowRequestMessage tellHowRequestMessage) {
        this(tellHowRequestMessage.getId(),
                tellHowRequestMessage.getServiceType(),
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
    private ServiceType serviceType;

    /**
     * 请求数据
     */
    private Object data;

}
