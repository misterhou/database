package com.example.database.entity;

import lombok.Data;

import java.util.Objects;

/**
 * 返回的VO
 * */
@Data
public class ReturnVo {
    private String id;//序列号
   // private String cate;//对应的标准问题、小类
    /**
     * 图片路径
     * 无|具体路径
     * */
    private String jpgPath;
    /**
     * 输出结果
     * 无|具体结果
     * */
    private String results;
    /**
     * 表格数据
     * 无|表数据
     * */
    private Object tabData;
    /**
     * 指令类型
     * 运行指令|数据库查询指令
     * */
   // private String directiveType;
    /**
     * 模型抽取信息
     * */
   // private String directive;
    /**
     * 用户问题
     * */
   // private String question;

    /**
     * 挥手动作
     * 1：左侧挥手，2：左上侧挥手；4：中间挥手；无：无动作
     */
    private String poseId = "无";

    /**
     * 数据来源：
     * llm： 代表大模型
     * NanRui：代表问题来着南瑞
     */
    private String source = "llm";

}
