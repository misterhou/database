package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 线路负载率
 */
@Data
@ToString
public class LineLoadRate {

    /**
     * 线路名称
     */
    private String lineName;

    /**
     * 最大负载率
     */
    private String maxRate;

    /**
     * 实时负载率
     */
    private String realtimeRate;
}
