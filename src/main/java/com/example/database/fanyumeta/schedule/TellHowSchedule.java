package com.example.database.fanyumeta.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.database.fanyumeta.client.TellHowClient;
import com.example.database.fanyumeta.client.vo.TellHowCurveVO;
import com.example.database.fanyumeta.entity.TotalLoad;
import com.example.database.fanyumeta.entity.ZoneLoad;
import com.example.database.fanyumeta.mapper.TotalLoadMapper;
import com.example.database.fanyumeta.mapper.ZoneLoadMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Component
@Slf4j
public class TellHowSchedule {

    @Resource
    private TellHowClient tellHowClient;

    @Resource
    private TotalLoadMapper totalLoadMapper;

    @Resource
    private ZoneLoadMapper zoneLoadMapper;

    /**
     * 总负荷数据更新
     */
    @Scheduled(cron = "30 59 23 * * ?")
    public void getTotalLoadCurve() {
        log.info("总负荷数据更新开始");
        TellHowCurveVO tellHowCurveVO = this.tellHowClient.totalLoadCurve(LocalDate.now());
        if (null != tellHowCurveVO) {
            TotalLoad totalLoad = new TotalLoad();
            totalLoad.setRecordDate(LocalDate.now());
            totalLoad.setMorningMaxValue(tellHowCurveVO.getMorningMaxValue());
            totalLoad.setEveningMaxValue(tellHowCurveVO.getEveningMaxValue());
            totalLoad.setDateMaxValue(tellHowCurveVO.getDateMaxValue());
            totalLoad.setUpdateTime(LocalDateTime.now());
            List<TotalLoad> totalLoadList = this.totalLoadMapper.selectList(
                    Wrappers.<TotalLoad>lambdaQuery()
                            .eq(TotalLoad::getRecordDate, totalLoad.getRecordDate()));
            if (totalLoadList.size() > 0) {
                log.info("新增总负荷数据");
                TotalLoad oldTotalLoad = totalLoadList.get(0);
                totalLoad.setId(oldTotalLoad.getId());
                this.totalLoadMapper.updateById(totalLoad);
            } else {
                log.info("更新总负荷数据");
                totalLoad.setCreateTime(totalLoad.getUpdateTime());
                this.totalLoadMapper.insert(totalLoad);
            }
        }
        log.info("总负荷数据更新结束");
    }

    /**
     * 分片区负荷数据更新
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void getZoneLoadCurve() {
        log.info("分片区负荷数据更新开始");
        TellHowClient.Area[] enumConstants = TellHowClient.Area.class.getEnumConstants();
        for (TellHowClient.Area area : enumConstants) {
            this.updateZoneLoad(area);
        }
        log.info("分片区负荷数据更新结束");
    }

    /**
     * 更新分片区负荷数据
     * @param area 区域
     */
    private void updateZoneLoad(TellHowClient.Area area) {
        log.info("更新{}分片区负荷数据开始", area.name());
        TellHowCurveVO tellHowCurveVO = this.tellHowClient.zoneLoadCurve(LocalDate.now(), area);
        if (null != tellHowCurveVO) {
            ZoneLoad zoneLoad = new ZoneLoad();
            zoneLoad.setAreaId(area.getValue());
            zoneLoad.setRecordDate(LocalDate.now());
            zoneLoad.setMorningMaxValue(tellHowCurveVO.getMorningMaxValue());
            zoneLoad.setEveningMaxValue(tellHowCurveVO.getEveningMaxValue());
            zoneLoad.setDateMaxValue(tellHowCurveVO.getDateMaxValue());
            zoneLoad.setUpdateTime(LocalDateTime.now());
            List<ZoneLoad> zoneLoadList = this.zoneLoadMapper.selectList(new LambdaQueryWrapper<ZoneLoad>()
                    .eq(ZoneLoad::getAreaId, zoneLoad.getAreaId())
                    .eq(ZoneLoad::getRecordDate, zoneLoad.getRecordDate()));
            if (zoneLoadList.size() > 0) {
                log.info("更新{}分片区负荷数据", area.name());
                ZoneLoad oldZoneLoad = zoneLoadList.get(0);
                zoneLoad.setId(oldZoneLoad.getId());
                this.zoneLoadMapper.updateById(zoneLoad);
            } else {
                log.info("新增{}分片区负荷数据", area.name());
                zoneLoad.setCreateTime(zoneLoad.getUpdateTime());
                this.zoneLoadMapper.insert(zoneLoad);
            }
        }
        log.info("更新{}分片区负荷数据结束", area.name());
    }
}
