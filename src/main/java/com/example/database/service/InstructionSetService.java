package com.example.database.service;

import com.example.database.domain.InstructionSet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.database.entity.InterlocutionResult;
import com.example.database.entity.ReturnVo;

/**
* @author woshi
* @description 针对表【instruction_set】的数据库操作Service
* @createDate 2023-09-20 09:56:26
*/
public interface InstructionSetService extends IService<InstructionSet> {
   public void haveReturnVo(InterlocutionResult ilResult,ReturnVo returnVo);
   public String getEnv();
   public String getLargeModelUrl();
}
