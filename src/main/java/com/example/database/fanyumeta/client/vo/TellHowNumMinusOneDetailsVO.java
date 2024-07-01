package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪N-1 明细值对象
 */
@Data
@ToString
public class TellHowNumMinusOneDetailsVO {

    /**
     * N-1 明细
     */
    private List<NumMinusOneDetails> numMinusOneDetailsList;

    /**
     * 挥手动作
     */
    private String poseId;
}
