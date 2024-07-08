package com.example.database.fanyumeta.component;

import com.example.database.fanyumeta.mapper.TotalLoadMapper;
import com.example.database.fanyumeta.mapper.ZoneLoadMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @deprecated 该功能由 {@link com.example.database.fanyumeta.component.AppInit } AppInit 完成
 */
@Slf4j
@Component
@Deprecated
public class DBCheck {

    private TotalLoadMapper totalLoadMapper;

    private ZoneLoadMapper zoneLoadMapper;

    public DBCheck(TotalLoadMapper totalLoadMapper, ZoneLoadMapper zoneLoadMapper) {
        this.totalLoadMapper = totalLoadMapper;
        this.zoneLoadMapper = zoneLoadMapper;
    }

    public void checkTableExist() {
        log.info("检查数据库表totalLoad");
        this.totalLoadMapper.createTableIfNotExists();
        log.info("检查数据库表zoneLoad");
        this.zoneLoadMapper.createTableIfNotExists();
    }
}
