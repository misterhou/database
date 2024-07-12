package com.example.database.fanyumeta.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("t_zone_load")
@Data
public class ZoneLoad extends MaxLoad{

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 区域
     */
    private String areaId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
