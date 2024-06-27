-- 总负荷表 --
CREATE TABLE IF NOT EXISTS `t_total_load`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `morning_max_value` varchar(50) NULL COMMENT '早峰最高负荷（8-12点）',
  `evening_max_value` varchar(50) NULL COMMENT '晚峰最高负荷（12-20点）',
  `date_max_value` varchar(50) NULL COMMENT '今日最高负荷',
  `record_date` datetime NULL COMMENT '记录日期',
  `create_time` datetime NULL COMMENT '创建日期',
  `update_time` datetime NULL COMMENT '更新日期',
  PRIMARY KEY (`id`)
);

-- 分区负荷表 --
CREATE TABLE IF NOT EXISTS `t_zone_load`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area_id` char(5) NULL COMMENT '区域编号（安新县：1，容城县：2，雄县县城：3），沧州片区：4，容东片区：5，容西片区：6，雄东片区：7，启动区：8，目标电网：9，雄县(含沧州)：10',
  `morning_max_value` varchar(50) NULL COMMENT '早峰最高负荷（8-12点）',
  `evening_max_value` varchar(50) NULL COMMENT '晚峰最高负荷（12-20点）',
  `date_max_value` varchar(50) NULL COMMENT '今日最高负荷',
  `record_date` datetime NULL COMMENT '记录日期',
  `create_time` datetime NULL COMMENT '创建日期',
  `update_time` datetime NULL COMMENT '更新日期',
  PRIMARY KEY (`id`)
);