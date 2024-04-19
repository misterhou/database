package com.example.database.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName generatrix
 */
@TableName(value ="generatrix")
@Data
public class Generatrix implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 所属场站
     */
    private String affiliatedStation;

    /**
     * 电压等级
     */
    private String voltageLevel;

    /**
     * 电压上限
     */
    private Double upperVoltageLimit;

    /**
     * 电压下限
     */
    private Double lowerVoltageLimit;

    /**
     * 预警上限
     */
    private Double warningUpperLimit;

    /**
     * 预警下限
     */
    private Double warningLowerLimit;

    /**
     * 线电压
     */
    private Double lineVoltage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}