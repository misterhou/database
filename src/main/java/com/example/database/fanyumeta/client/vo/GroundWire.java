package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 地线
 */
@Data
@ToString
public class GroundWire {

    /**
     * 序号
     */
    private String number;

    /**
     * 地线名称
     */
    private String groundWireName;

    /**
     * 地线悬挂时间
     */
    private String groundWireCreateTime;

    /**
     * 装置位置
     */
    private String devicePosition;

    /**
     * 装设人
     */
    private String groundWireSuspensionCreateUser;

    /**
     * 装设时间
     */
    private String groundWireSuspensionCreateTime;
}
