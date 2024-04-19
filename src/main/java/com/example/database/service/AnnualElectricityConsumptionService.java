package com.example.database.service;

import com.example.database.domain.AnnualElectricityConsumption;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author woshi
* @description 针对表【annual_electricity_consumption】的数据库操作Service
* @createDate 2023-09-20 09:34:31
*/
public interface AnnualElectricityConsumptionService extends IService<AnnualElectricityConsumption> {

//    double getMaxTime(String data);

    List<AnnualElectricityConsumption> getList(String date);

}
