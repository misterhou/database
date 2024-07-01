package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 检修单
 */
@Data
@ToString
public class OverhaulWorkList {

    /**
     * 批准开始时间
     */
    private String startTime;

    /**
     * 批准结束时间
     */
    private String endTime;

    /**
     * 环节状态
     */
    private String status;

    /**
     * 工作类型
     */
    private String powercutType;

    /**
     * 工作要求
     */
    private String workContent;
}
