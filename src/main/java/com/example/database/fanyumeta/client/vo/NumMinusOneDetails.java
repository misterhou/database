package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * N-1 设备明细
 */
@Data
@ToString
public class NumMinusOneDetails {

    /**
     * 序号
     */
    private String number;

    /**
     * 设备名称
     */
    private String devName;

    /**
     * 厂站名称
     */
    private String stName;
}
