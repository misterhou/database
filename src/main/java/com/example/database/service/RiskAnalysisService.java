package com.example.database.service;

import com.example.database.domain.RiskAnalysis;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author woshi
* @description 针对表【risk_analysis】的数据库操作Service
* @createDate 2023-09-20 09:56:38
*/
public interface RiskAnalysisService extends IService<RiskAnalysis> {

    List<RiskAnalysis> getRiskAnalysis(String stationName);
}
