package com.example.database.fanyumeta.client.vo;

import lombok.Data;
import lombok.ToString;

/**
 * 保电信息
 */
@Data
@ToString
public class PowerSupplyInfo {

    /**
     * 保电开始时间
     */
    private String startTime;

    /**
     * 保电任务名称
     */
    private String taskName;

    /**
     * 任务下的保电地点列表(保电用户)
     */
    private String userList;
}
