package com.example.database.fanyumeta.transfer.vo;

import com.example.database.fanyumeta.utils.StringUtils;
import lombok.Data;

/**
 * 中转服务响应实体
 */
@Data
public class TransferResponseBody {

    /**
     * 响应 di
     */
    private String resId;

    /**
     * 服务类型
     */
    private String service;

    /**
     * 响应结果
     */
    private Boolean result;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private Object data;

    public TransferResponseBody() {
        this.resId = StringUtils.getUUID();
        this.result = false;
        this.message = "服务异常";
    }
}
