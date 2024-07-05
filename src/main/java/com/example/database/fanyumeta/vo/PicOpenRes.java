package com.example.database.fanyumeta.vo;

import lombok.Data;

/**
 * 第三方开图响应
 */
@Data
public class PicOpenRes {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 处理成功响应
     */
    public static PicOpenRes success() {
        PicOpenRes res = new PicOpenRes();
        res.setCode(0);
        res.setMessage("success");
        return res;
    }

    /**
     * 图片名称为空响应
     */
    public static PicOpenRes picNameIsBlank() {
        PicOpenRes res = new PicOpenRes();
        res.setCode(1001);
        res.setMessage("图片名称不能为空");
        return res;
    }

    /**
     * 图片类型为空响应
     */
    public static PicOpenRes picTypeIsBlank() {
        PicOpenRes res = new PicOpenRes();
        res.setCode(1002);
        res.setMessage("图片类型不能为空");
        return res;
    }

    /**
     * 图片不存在响应
     */
    public static PicOpenRes picNameNotFound() {
        PicOpenRes res = new PicOpenRes();
        res.setCode(1003);
        res.setMessage("图片不存在");
        return res;
    }

    /**
     * 图片类型不正确响应
     */
    public static PicOpenRes picTypeNotFound() {
        PicOpenRes res = new PicOpenRes();
        res.setCode(1004);
        res.setMessage("图片类型不正确");
        return res;
    }
}
