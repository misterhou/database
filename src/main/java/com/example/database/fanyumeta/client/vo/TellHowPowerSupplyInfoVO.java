package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪保电信息值对象
 */
@Data
@ToString
public class TellHowPowerSupplyInfoVO {

    /**
     * 保电信息列表
     */
    private List<PowerSupplyInfo> powerSupplyInfoList;

    /**
     * 挥手动作
     */
    private String poseId;
}
