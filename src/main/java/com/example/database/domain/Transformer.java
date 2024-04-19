package com.example.database.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName transformer
 */
@TableName(value ="transformer")
@Data
public class Transformer implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 设备名称
     */
    private String equipmentName;

    /**
     * 所属场站
     */
    private String factoryStation;

    /**
     * 额定容量
     */
    private Double capacity;

    /**
     * 最高电压等级
     */
    private String maxVoltage;

    /**
     * 绕组类型
     */
    private String windingType;

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
        Transformer other = (Transformer) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEquipmentName() == null ? other.getEquipmentName() == null : this.getEquipmentName().equals(other.getEquipmentName()))
            && (this.getFactoryStation() == null ? other.getFactoryStation() == null : this.getFactoryStation().equals(other.getFactoryStation()))
            && (this.getCapacity() == null ? other.getCapacity() == null : this.getCapacity().equals(other.getCapacity()))
            && (this.getMaxVoltage() == null ? other.getMaxVoltage() == null : this.getMaxVoltage().equals(other.getMaxVoltage()))
            && (this.getWindingType() == null ? other.getWindingType() == null : this.getWindingType().equals(other.getWindingType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEquipmentName() == null) ? 0 : getEquipmentName().hashCode());
        result = prime * result + ((getFactoryStation() == null) ? 0 : getFactoryStation().hashCode());
        result = prime * result + ((getCapacity() == null) ? 0 : getCapacity().hashCode());
        result = prime * result + ((getMaxVoltage() == null) ? 0 : getMaxVoltage().hashCode());
        result = prime * result + ((getWindingType() == null) ? 0 : getWindingType().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", equipmentName=").append(equipmentName);
        sb.append(", factoryStation=").append(factoryStation);
        sb.append(", capacity=").append(capacity);
        sb.append(", maxVoltage=").append(maxVoltage);
        sb.append(", windingType=").append(windingType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}