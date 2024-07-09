package com.example.database.fanyumeta.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.database.fanyumeta.entity.MaxLoad;
import com.example.database.fanyumeta.entity.TotalLoad;
import com.example.database.fanyumeta.entity.ZoneLoad;
import com.example.database.fanyumeta.mapper.TotalLoadMapper;
import com.example.database.fanyumeta.mapper.ZoneLoadMapper;
import com.example.database.fanyumeta.service.LoadService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoadServiceImpl implements LoadService {

    private TotalLoadMapper totalLoadMapper;

    private ZoneLoadMapper zoneLoadMapper;

    public LoadServiceImpl(TotalLoadMapper totalLoadMapper, ZoneLoadMapper zoneLoadMapper) {
        this.totalLoadMapper = totalLoadMapper;
        this.zoneLoadMapper = zoneLoadMapper;
    }


    @Override
    public List<MaxLoad> getTotalLoadByRecordDate(LocalDate... recordDate) {
        List<MaxLoad> maxLoadList = null;
        List<TotalLoad> totalLoads = this.totalLoadMapper.selectList(
                Wrappers.<TotalLoad>lambdaQuery()
                        .in(TotalLoad::getRecordDate, recordDate)
        );
        if (!ObjectUtils.isEmpty(totalLoads)) {
            maxLoadList = totalLoads.stream().map(totalLoad -> {
                MaxLoad maxLoad = new MaxLoad();
                maxLoad.setRecordDate(totalLoad.getRecordDate());
                maxLoad.setDateMaxValue(totalLoad.getDateMaxValue());
                return maxLoad;
            }).collect(Collectors.toList());
        }
        return maxLoadList;
    }

    @Override
    public List<MaxLoad> getZoneLoadByRecordDate(String area, LocalDate... recordDate) {
        List<MaxLoad> maxLoadList = null;
        List<ZoneLoad> zoneLoads = this.zoneLoadMapper.selectList(
                Wrappers.<ZoneLoad>lambdaQuery()
                        .eq(ZoneLoad::getAreaId, area)
                        .in(ZoneLoad::getRecordDate, recordDate)
        );
        if (!ObjectUtils.isEmpty(zoneLoads)) {
            maxLoadList = zoneLoads.stream().map(zoneLoad -> {
                MaxLoad maxLoad = new MaxLoad();
                maxLoad.setRecordDate(zoneLoad.getRecordDate());
                maxLoad.setDateMaxValue(zoneLoad.getDateMaxValue());
                return maxLoad;
            }).collect(Collectors.toList());
        }

        return maxLoadList;
    }


}
