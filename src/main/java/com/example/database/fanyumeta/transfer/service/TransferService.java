package com.example.database.fanyumeta.transfer.service;

import com.example.database.fanyumeta.transfer.vo.TransferRequestBody;
import com.example.database.fanyumeta.transfer.vo.TransferResponseBody;

/**
 * 中转服务
 */
public interface TransferService {

    /**
     * 将泰豪请求数据发个广哈
     *
     * @param transferRequestBody 请求数据
     * @return 响应数据
     */
    TransferResponseBody sendMsg2Gh(TransferRequestBody transferRequestBody);


    /**
     * 将广哈请求数据发个泰豪
     *
     * @param transferRequestBody 请求数据
     * @return 响应数据
     */
    TransferResponseBody sendMsg2Th(TransferRequestBody transferRequestBody);
}
