package com.example.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.domain.RiskAnalysis;
import com.example.database.service.RiskAnalysisService;
import com.example.database.mapper.RiskAnalysisMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author woshi
* @description 针对表【risk_analysis】的数据库操作Service实现
* @createDate 2023-09-20 09:56:38
*/
@Service
public class RiskAnalysisServiceImpl extends ServiceImpl<RiskAnalysisMapper, RiskAnalysis>
    implements RiskAnalysisService{

    @Override
    public List<RiskAnalysis> getRiskAnalysis(String stationName) {
        QueryWrapper<RiskAnalysis> queryWrapper=new QueryWrapper<>();
        queryWrapper.like("station_name",stationName);
        return this.list(queryWrapper);
    }
}




