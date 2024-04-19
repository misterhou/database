package com.example.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.domain.Line;
import com.example.database.service.LineService;
import com.example.database.mapper.LineMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author woshi
* @description 针对表【line】的数据库操作Service实现
* @createDate 2023-09-20 09:56:29
*/
@Service
public class LineServiceImpl extends ServiceImpl<LineMapper, Line>
    implements LineService{

    @Override
    public List<Line> getLine(String voltageLevel) {
        QueryWrapper<Line> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("voltage_level",voltageLevel);
        List<Line> list=this.list(queryWrapper);
        return list;
    }
}




