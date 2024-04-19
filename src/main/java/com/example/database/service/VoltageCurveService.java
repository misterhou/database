package com.example.database.service;

import com.example.database.domain.VoltageCurve;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author woshi
* @description 针对表【voltage_curve】的数据库操作Service
* @createDate 2023-09-20 09:56:44
*/
public interface VoltageCurveService extends IService<VoltageCurve> {

    List<VoltageCurve> getList(String s);
}
