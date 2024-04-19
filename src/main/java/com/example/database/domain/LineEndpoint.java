package com.example.database.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName line_endpoint
 */
@TableName(value ="line_endpoint")
@Data
public class LineEndpoint implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 线端名称
     */
    private String lineEndName;

    /**
     * 厂站id
     */
    private String stationId;

    /**
     * 电压等级
     */
    private String voltageLevel;

    /**
     * 所属线路
     */
    private String line;

    /**
     * 有功值
     */
    private Double activeValue;

    /**
     * 无功值
     */
    private Double reactivePowerValue;

    /**
     * 电流值
     */
    private Double current;

    /**
     * 功率因数
     */
    private Double powerFactor;

    /**
     * 电流负载率
     */
    private Double currentLoadRate;

    /**
     * 视在功率
     */
    private Double apparentPower;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LineEndpoint other = (LineEndpoint) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getLineEndName() == null ? other.getLineEndName() == null : this.getLineEndName().equals(other.getLineEndName()))
            && (this.getStationId() == null ? other.getStationId() == null : this.getStationId().equals(other.getStationId()))
            && (this.getVoltageLevel() == null ? other.getVoltageLevel() == null : this.getVoltageLevel().equals(other.getVoltageLevel()))
            && (this.getLine() == null ? other.getLine() == null : this.getLine().equals(other.getLine()))
            && (this.getActiveValue() == null ? other.getActiveValue() == null : this.getActiveValue().equals(other.getActiveValue()))
            && (this.getReactivePowerValue() == null ? other.getReactivePowerValue() == null : this.getReactivePowerValue().equals(other.getReactivePowerValue()))
            && (this.getCurrent() == null ? other.getCurrent() == null : this.getCurrent().equals(other.getCurrent()))
            && (this.getPowerFactor() == null ? other.getPowerFactor() == null : this.getPowerFactor().equals(other.getPowerFactor()))
            && (this.getCurrentLoadRate() == null ? other.getCurrentLoadRate() == null : this.getCurrentLoadRate().equals(other.getCurrentLoadRate()))
            && (this.getApparentPower() == null ? other.getApparentPower() == null : this.getApparentPower().equals(other.getApparentPower()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getLineEndName() == null) ? 0 : getLineEndName().hashCode());
        result = prime * result + ((getStationId() == null) ? 0 : getStationId().hashCode());
        result = prime * result + ((getVoltageLevel() == null) ? 0 : getVoltageLevel().hashCode());
        result = prime * result + ((getLine() == null) ? 0 : getLine().hashCode());
        result = prime * result + ((getActiveValue() == null) ? 0 : getActiveValue().hashCode());
        result = prime * result + ((getReactivePowerValue() == null) ? 0 : getReactivePowerValue().hashCode());
        result = prime * result + ((getCurrent() == null) ? 0 : getCurrent().hashCode());
        result = prime * result + ((getPowerFactor() == null) ? 0 : getPowerFactor().hashCode());
        result = prime * result + ((getCurrentLoadRate() == null) ? 0 : getCurrentLoadRate().hashCode());
        result = prime * result + ((getApparentPower() == null) ? 0 : getApparentPower().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", lineEndName=").append(lineEndName);
        sb.append(", stationId=").append(stationId);
        sb.append(", voltageLevel=").append(voltageLevel);
        sb.append(", line=").append(line);
        sb.append(", activeValue=").append(activeValue);
        sb.append(", reactivePowerValue=").append(reactivePowerValue);
        sb.append(", current=").append(current);
        sb.append(", powerFactor=").append(powerFactor);
        sb.append(", currentLoadRate=").append(currentLoadRate);
        sb.append(", apparentPower=").append(apparentPower);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}