package com.example.database.fanyumeta.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.database.fanyumeta.entity.TotalLoad;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TotalLoadMapper extends BaseMapper<TotalLoad> {

    @Update("CREATE TABLE IF NOT EXISTS `t_total_load`  (\n" +
            "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
            "  `morning_max_value` varchar(50) NULL COMMENT '早峰最高负荷（8-12点）',\n" +
            "  `evening_max_value` varchar(50) NULL COMMENT '晚峰最高负荷（12-20点）',\n" +
            "  `date_max_value` varchar(50) NULL COMMENT '今日最高负荷',\n" +
            "  `record_date` datetime NULL COMMENT '记录日期',\n" +
            "  `create_time` datetime NULL COMMENT '创建日期',\n" +
            "  `update_time` datetime NULL COMMENT '更新日期',\n" +
            "  PRIMARY KEY (`id`)\n" +
            ");")
    void createTableIfNotExists();
}
