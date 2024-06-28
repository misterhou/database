package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪当前电网故障详情
 */
@Data
@ToString
public class TellHowCurrentGridFailureVO {

    /**
     * 当前电网故障详情
     */
    private List<CurrentGridFailure> currentGridFailureList;

    /**
     * 挥手动作
     */
    private String poseId;
}
