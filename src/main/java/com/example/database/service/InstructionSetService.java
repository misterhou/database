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

   /**
    * 8017 响应数据处理
    * @param ilResult 8017 响应数据
    * @param returnVo 处理结果
    * @param message 接收到的中控指令信息
    */
   public void haveReturnVo(InterlocutionResult ilResult,ReturnVo returnVo, String message);
   public String getEnv();
   public String getLargeModelUrl();
}
