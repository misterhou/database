package com.example.database.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName instruction_set
 */
@TableName(value ="instruction_set")
@Data
public class InstructionSet implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 场景大类
     */
    private String sceneCategories;

    /**
     * 小类
     */
    private String subclass;

    /**
     * 说明
     */
    private String illustrate;

    /**
     * 模板信息
     * */
    private String templateDesc;
    /**
     * 状态
     * */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}