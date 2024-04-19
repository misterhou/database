package com.example.database.mapper;

import com.example.database.domain.AnnualElectricityConsumption;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author woshi
* @description 针对表【annual_electricity_consumption】的数据库操作Mapper
* @createDate 2023-09-20 09:34:31
* @Entity com.example.database.domain.AnnualElectricityConsumption
*/
public interface AnnualElectricityConsumptionMapper extends BaseMapper<AnnualElectricityConsumption> {

    List<AnnualElectricityConsumption> getMaxTime(String data);
}




