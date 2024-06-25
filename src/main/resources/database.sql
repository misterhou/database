-- 总负荷表 --
CREATE TABLE `real_time_database`.`t_total_load`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `morning_max_value` varchar(50) NULL COMMENT '早峰最高负荷（8-12点）',
  `evening_max_value` varchar(50) NULL COMMENT '晚峰最高负荷（12-20点）',
  `date_max_value` varchar(50) NULL COMMENT '今日最高负荷',
  `record_date` datetime NULL COMMENT '记录日期',
  `create_time` datetime NULL COMMENT '创建日期',
  `update_time` datetime NULL COMMENT '更新日期',
  PRIMARY KEY (`id`)
);