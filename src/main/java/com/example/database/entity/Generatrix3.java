package com.example.database.entity;

import lombok.Data;

@Data
public class Generatrix3 {
    private String name;
    /**
     * 电压上限
     */
    private Double upperVoltageLimit;

    /**
     * 电压下限
     */
    private Double lowerVoltageLimit;
}
