package com.example.database.service;

import com.example.database.domain.Line;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author woshi
* @description 针对表【line】的数据库操作Service
* @createDate 2023-09-20 09:56:29
*/
public interface LineService extends IService<Line> {
    List<Line> getLine(String voltageLevel);
}
