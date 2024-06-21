package com.example.database.fanyumeta.server;

/**
 * 服务类型
 */
public enum ServiceType {

    /**
     * 开图
     */
    KAI_TU,

    /**
     * 大屏指标
     */
    ZHIBIAO,

    /**
     * 呼出（泰豪呼出应用，需要调用广哈呼出接口）
     */
    CALL_OUT,

    /**
     * 关闭开图窗口
     */
    CLOSE_NOTICE
}
