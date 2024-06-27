package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransLoadRate {

    /**
     * 主变名称
     */
    private String devName;

    /**
     * 厂站名称
     */
    private String stName;

    /**
     * 最大负载率
     */
    private String maxRate;

    /**
     * 实时负载率
     */
    private String realtimeRate;

    /**
     * 主变全称
     */
    private String fullName;
}
