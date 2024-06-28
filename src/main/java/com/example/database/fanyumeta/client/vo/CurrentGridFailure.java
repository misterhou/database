package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 当前电网故障
 */
@Data
@ToString
public class CurrentGridFailure {

    /**
     * 故障简述
     */
    private String faultDescribe;

    /**
     * 故障类型
     */
    private String faultType;

    /**
     * 流程环节状态
     */
    private String status;

    /**
     * 故障发生时间
     */
    private String faultStartTime;
}
