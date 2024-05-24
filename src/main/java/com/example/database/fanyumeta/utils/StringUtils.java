package com.example.database.fanyumeta.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 获取 UUID 字符串
     * @return UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取时间戳
     * @return 时间戳
     */
    public static String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
