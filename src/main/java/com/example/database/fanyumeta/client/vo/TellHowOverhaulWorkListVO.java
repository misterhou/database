package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪检修单明细值对象
 */
@Data
@ToString
public class TellHowOverhaulWorkListVO {

    /**
     * 检修单明细
     */
    private List<OverhaulWorkList> overhaulWorkListList;

    /**
     * 挥手动作
     */
    private String poseId;
}
