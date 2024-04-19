package com.example.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.domain.RiskAnalysis;
import com.example.database.domain.Transformer;
import com.example.database.service.TransformerService;
import com.example.database.mapper.TransformerMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author woshi
* @description 针对表【transformer】的数据库操作Service实现
* @createDate 2023-09-20 09:56:41
*/
@Service
public class TransformerServiceImpl extends ServiceImpl<TransformerMapper, Transformer>
    implements TransformerService{

    @Override
    public Long getTransformer(String maxVoltage) {
        QueryWrapper<Transformer> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("max_voltage",maxVoltage);
        long count = this.count(queryWrapper);
        return count;
    }
}




