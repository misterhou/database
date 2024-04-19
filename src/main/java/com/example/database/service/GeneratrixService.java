package com.example.database.service;

import com.example.database.domain.Generatrix;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author woshi
* @description 针对表【generatrix】的数据库操作Service
* @createDate 2023-09-20 09:56:22
*/
public interface GeneratrixService extends IService<Generatrix> {

    List<Generatrix> getGeneratrix(String name);

    List<Generatrix> getGeneratrix2(String kv);
}
