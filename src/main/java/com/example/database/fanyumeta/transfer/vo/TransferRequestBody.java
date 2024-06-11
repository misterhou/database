package com.example.database.fanyumeta.transfer.vo;

import lombok.Data;

/**
 * 中转服务请求参数
 */
@Data
public class TransferRequestBody {

    /**
     * 请求 id
     */
    private String reqId;

    /**
     * 请求服务
     */
    private String service;

    /**
     * 请求数据
     */
    private Object data;
}
