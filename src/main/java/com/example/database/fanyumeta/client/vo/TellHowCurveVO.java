package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 泰豪负荷曲线值对象（非全部字段）
 */
@Data
@ToString
public class TellHowCurveVO {

    /**
     * 早峰最大负荷
     */
    private String morningMaxValue;

    /**
     * 晚峰最大负荷
     */
    private String eveningMaxValue;

    /**
     * 日期最大负荷
     */
    private String dateMaxValue;

    /**
     * 数智人挥手动作
     */
    private String poseId;

    /**
     * 与昨天比较
     */
    private String compareYesterday;

    /**
     * 与去年比较
     */
    private String compareLastYear;
}
