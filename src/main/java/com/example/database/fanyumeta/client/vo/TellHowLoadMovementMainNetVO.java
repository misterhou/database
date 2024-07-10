package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪负荷走势预测-主网值对象
 */
@Data
@ToString
public class TellHowLoadMovementMainNetVO {

    /**
     * 日期时间集合
     */
    private List<String> dateTime;

    /**
     * 今日实际负荷
     */
    private List<String> todayActualLoad;

    /**
     * 昨日实际负荷
     */
    private List<String> yesterdayActualLoad;

    /**
     * 主网预测负荷
     */
    private List<String> predictLoad;

    /**
     * 挥手动作
     */
    private String poseId;
}
