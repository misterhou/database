package com.example.database.fanyumeta.component;

import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import com.example.database.fanyumeta.utils.PicDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 应用启动时初始化
 */
@Component
@Slf4j
public class AppInit implements ApplicationListener<ApplicationReadyEvent> {

    private Map<String, String> tableMap = new HashMap<>();

    public AppInit() {
        // 初始化系统表与建表语句映射
        tableMap.put("t_total_load", "CREATE TABLE IF NOT EXISTS `t_total_load`  (\n" +
                "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `morning_max_value` varchar(50) NULL COMMENT '早峰最高负荷（8-12点）',\n" +
                "  `evening_max_value` varchar(50) NULL COMMENT '晚峰最高负荷（12-20点）',\n" +
                "  `date_max_value` varchar(50) NULL COMMENT '今日最高负荷',\n" +
                "  `record_date` datetime NULL COMMENT '记录日期',\n" +
                "  `create_time` datetime NULL COMMENT '创建日期',\n" +
                "  `update_time` datetime NULL COMMENT '更新日期',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");");
        tableMap.put("t_zone_load", "CREATE TABLE IF NOT EXISTS `t_zone_load`  (\n" +
                "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `area_id` char(5) NULL COMMENT '区域编号（安新县：1，容城县：2，雄县县城：3），沧州片区：4，容东片区：5，容西片区：6，雄东片区：7，启动区：8，目标电网：9，雄县(含沧州)：10',\n" +
                "  `morning_max_value` varchar(50) NULL COMMENT '早峰最高负荷（8-12点）',\n" +
                "  `evening_max_value` varchar(50) NULL COMMENT '晚峰最高负荷（12-20点）',\n" +
                "  `date_max_value` varchar(50) NULL COMMENT '今日最高负荷',\n" +
                "  `record_date` datetime NULL COMMENT '记录日期',\n" +
                "  `create_time` datetime NULL COMMENT '创建日期',\n" +
                "  `update_time` datetime NULL COMMENT '更新日期',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");");
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ConfigurableApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        this.initPicDataCache(applicationContext);
        this.checkTables(applicationContext);
    }

    /**
     * 检查数据库表
     *
     * @param ctx spring 上下文
     */
    private void checkTables(ConfigurableApplicationContext ctx) {
        log.info("检查数据库表-开始");
        DataSource dataSource = ctx.getBean(DataSource.class);
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            String catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(catalog, null, "%", new String[]{"TABLE"});
            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            for (String checkTable : this.tableMap.keySet()) {
                if (!tableNames.contains(checkTable)) {
                    log.info("创建数据库表-{}", checkTable);
                    String createTableSql = this.tableMap.get(checkTable);
                    PreparedStatement preparedStatement = connection.prepareStatement(createTableSql);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } else {
                    log.info("数据库表-{}-已存在", checkTable);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("检查数据库表异常", e);
                }
            }
        }
        log.info("检查数据库表-结束");
    }

    /**
     * 初始化图片数据缓存
     *
     * @param ctx spring 上下文
     */
    private void initPicDataCache(ConfigurableApplicationContext ctx) {
        Environment environment = ctx.getEnvironment();
        HardwareControlCommandUtil.initCache(
                environment.getProperty("fan-yu.hardware-control.request-command-config-file"),
                environment.getProperty("fan-yu.hardware-control.receive-command-config-file"));
        PicDataUtil.initPicData(environment.getProperty("fan-yu.tell-how.pic-config-file"));
        PicDataUtil.initSubstationData(environment.getProperty("fan-yu.tell-how.substation-config-file"));
        PicDataUtil.initSourcePicData(environment.getProperty("fan-yu.tell-how.source-config-file"));
    }
}
