package com.example.database.fanyumeta.client.vo;

import lombok.Data;

/**
 * 泰豪响应值对象
 */
@Data
public class TellHowVO {

    /**
     * 播报内容
     */
    private String voiceContent;

    /**
     * 挥手动作
     */
    private String poseId;
}
