package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 电网风险
 */
@Data
@ToString
public class GridRisk {

    /**
     * 序号
     */
    private String number;

    /**
     * 单位厂站名称
     */
    private String stationName;

    /**
     * 风险等级
     */
    private String eventLevel;


}
