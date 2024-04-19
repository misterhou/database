package com.example.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.domain.Generatrix;
import com.example.database.service.GeneratrixService;
import com.example.database.mapper.GeneratrixMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author woshi
* @description 针对表【generatrix】的数据库操作Service实现
* @createDate 2023-09-20 09:56:22
*/
@Service
public class GeneratrixServiceImpl extends ServiceImpl<GeneratrixMapper, Generatrix>
    implements GeneratrixService{

    @Override
    public List<Generatrix> getGeneratrix(String name) {
        QueryWrapper<Generatrix> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",name);
        return this.list(queryWrapper);
    }

    @Override
    public List<Generatrix> getGeneratrix2(String kv) {
        QueryWrapper<Generatrix> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("voltage_level",kv);
        return this.list(queryWrapper);
    }
}




