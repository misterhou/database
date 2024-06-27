package com.example.database.fanyumeta.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("t_zone_load")
@Data
public class ZoneLoad {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String areaId;

    private String morningMaxValue;

    private String eveningMaxValue;

    private String dateMaxValue;

    private LocalDate recordDate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
