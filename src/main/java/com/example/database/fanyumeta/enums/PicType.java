package com.example.database.fanyumeta.enums;


/**
 * 图片类型
 */
public enum PicType {

    CONTACT(1, "联络图"),
    SOURCE(2, "溯源图"),
    TYPE_ERROR(999, "错误");

    private int code;
    private String desc;

    PicType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
