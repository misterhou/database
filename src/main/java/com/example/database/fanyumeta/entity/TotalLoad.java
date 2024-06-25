package com.example.database.fanyumeta.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("t_total_load")
@Data
public class TotalLoad {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String morningMaxValue;

    private String eveningMaxValue;

    private String dateMaxValue;

    private LocalDate recordDate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
