package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 泰豪获取地线值对象
 */
@Data
@ToString
public class TellHowGroundWireVO {

    /**
     * 地线列表
     */
    private List<GroundWire> groundWireList;

    /**
     * 挥手动作
     */
    private String poseId;
}
