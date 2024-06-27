package com.example.database.fanyumeta.client.vo;

import lombok.Data;

/**
 * 泰豪负荷曲线值对象（非全部字段）
 */
@Data
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
}
