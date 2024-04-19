package com.example.database.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName line
 */
@TableName(value ="line")
@Data
public class Line implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 线路名称
     */
    private String lineName;

//    /**
//     * 电压等级
//     */
//    private String voltageLevel;
//
//    /**
//     * 起点厂站
//     */
//    private String startingStation;
//
//    /**
//     * 起点所属间隔
//     */
//    private String startInterval;
//
//    /**
//     * 终点厂站
//     */
//    private String terminalStation;
//
//    /**
//     * 功率限值
//     */
//    private Double powerLimit;

    /**
     * 线路全长
     */
    private Double lineLength;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
//    @TableField(exist = false)
//    private Double lineAllLenth=0.0;
}
