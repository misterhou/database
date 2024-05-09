package com.example.database.fanyumeta.utils;

import java.util.UUID;

public class StringUtils {

    /**
     * 获取 UUID 字符串
     * @return UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
