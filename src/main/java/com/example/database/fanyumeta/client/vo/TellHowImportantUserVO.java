package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 泰豪重要用户
 */
@Data
@ToString
public class TellHowImportantUserVO {

    /**
     * 总数
     */
    private String total;

    /**
     * 挥手动作
     */
    private String poseId;
}
