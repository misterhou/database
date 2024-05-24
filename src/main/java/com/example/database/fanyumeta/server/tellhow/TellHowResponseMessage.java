package com.example.database.fanyumeta.server.tellhow;

import com.example.database.fanyumeta.utils.StringUtils;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 发送给泰豪的数据
 */
@Data
@Deprecated
public class TellHowResponseMessage {

    public TellHowResponseMessage() {
        this.id = StringUtils.getUUID();
        this.result = false;
        this.timestamp = StringUtils.getTimestamp();
        this.data = "";
    }

    public TellHowResponseMessage(String reqId, String data) {
        this();
        this.reqId = reqId;
        this.data = data;
    }

    /**
     * 响应唯一标识
     */
    private String id;

    /**
     * 请求唯一标识
     */
    private String reqId;

    /**
     * 响应结果
     */
    private Boolean result;

    /**
     * 响应时间
     */
    private String timestamp;

    /**
     * 功能
     */
    private String function;

    /**
     * 图片地址
     */
    private Object data;
}
