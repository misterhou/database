package com.example.database.service;

import com.example.database.domain.Transformer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author woshi
* @description 针对表【transformer】的数据库操作Service
* @createDate 2023-09-20 09:56:41
*/
public interface TransformerService extends IService<Transformer> {

    Long getTransformer(String maxVoltage);
}
