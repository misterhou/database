package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪线路负载率值对象
 */
@Data
@ToString
public class TellHowLineLoadRateVO {

    /**
     * 线路负载率
     */
    private List<LineLoadRate> lineLoadRateList;

    /**
     * 挥手动作
     */
    private String poseId;
}
