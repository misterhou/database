package com.example.database.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName risk_analysis
 */
@TableName(value ="risk_analysis")
@Data
public class RiskAnalysis implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 厂站ID
     */
    private String stationName;

    /**
     * 电压等级
     */
    private String voltageLevel;

    /**
     * 风险设备ID
     */
//    private String riskyDevices;

    /**
     * 损失负荷
     */
//    private String lossLoad;

    /**
     * 风险等级
     */
    private Integer riskLevel;

    /**
     * 厂站图形名称
     */
//    private String plantStationGraphics;

    /**
     * 风险类型
     */
    private Integer riskType;

    /**
     * 损失发电
     */
//    private Double lossGeneration;

    /**
     * 风险设备名称
     */
    private String riskEquipment;

    /**
     * 等级对应条款
     */
//    private String levelCorrespondingClause;

    /**
     * 可疑备自投动作风险
     */
    private String suspiciousRisk;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
