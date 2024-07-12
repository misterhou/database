package com.example.database.fanyumeta.entity;

import lombok.Data;

import java.time.LocalDate;

/**
 * 最大负荷
 */
@Data
public class MaxLoad {

    /**
     * 早峰最大负荷
     */
    private String morningMaxValue;

    /**
     * 晚峰最大负荷
     */
    private String eveningMaxValue;

    /**
     * 最大负荷
     */
    private String dateMaxValue;

    /**
     * 记录日期
     */
    private LocalDate recordDate;
}
