package com.example.database.fanyumeta.schedule;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.database.fanyumeta.client.TellHowClient;
import com.example.database.fanyumeta.entity.TotalLoad;
import com.example.database.fanyumeta.mapper.TotalLoadMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Scheduled(cron = "0 59 23 * * ?")
    public void getTotalLoadCurve() {
        log.info("定时任务-开始调用泰豪获取总负荷曲线数据接口");
        JSONObject data = this.tellHowClient.totalLoadCurve(LocalDate.now());
        if (null != data) {
            JSONObject resData = data.getJSONObject("resData");
            if (null != resData) {
                boolean isCreate = true;
                TotalLoad totalLoad = new TotalLoad();
                totalLoad.setRecordDate(LocalDate.now());
                LambdaQueryWrapper<TotalLoad> lambdaQueryWrapper = new LambdaQueryWrapper();
                lambdaQueryWrapper.eq(TotalLoad::getRecordDate, totalLoad.getRecordDate());
                List<TotalLoad> totalLoads = this.totalLoadMapper.selectList(lambdaQueryWrapper);
                if (totalLoads.size() > 0) {
                    isCreate = false;
                    totalLoad = totalLoads.get(0);
                }
                JSONObject todayCurve = resData.getJSONObject("todayCurve");
                if (null != todayCurve) {
                    String maxValue = todayCurve.getString("max");
                    if (StringUtils.isNotBlank(maxValue)) {
                        totalLoad.setDateMaxValue(maxValue);
                        if (isCreate) {
                            LocalDateTime createTime = LocalDateTime.now();
                            totalLoad.setCreateTime(createTime);
                            totalLoad.setUpdateTime(createTime);
                            this.totalLoadMapper.insert(totalLoad);
                        } else {
                            totalLoad.setUpdateTime(LocalDateTime.now());
                            this.totalLoadMapper.updateById(totalLoad);
                        }
                    }
                }
            }
        }
        System.out.println("getTotalLoadCurve");
    }
}
