package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪电网风险值对象
 */
@Data
@ToString
public class TellHowGridRiskVO {

    /**
     * 电网风险
     */
    private List<GridRisk> gridRiskList;

    /**
     * 挥手动作
     */
    private String poseId;
}
