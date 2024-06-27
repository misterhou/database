package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪主变负载率
 */
@Data
@ToString
public class TellHowTransLoadRateVO {

    /**
     * 主变负载率列表
     */
    private List<TransLoadRate> transLoadRateList;

    /**
     * 挥手动作
     */
    private String poseId;
}
