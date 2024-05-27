package com.example.database.fanyumeta.utils;

import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class StringUtils extends org.springframework.util.StringUtils {

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

    /**
     * 获取日期字符串（yyyy-MM-dd）
     * @param localDate 日期
     * @return 日期字符串
     */
    public static String getDateStr(LocalDate localDate) {
        Assert.notNull(localDate, "localDate 参数不能为空");
        return localDate.format(DateTimeFormatter.ISO_DATE);
    }

    /**
     * 获取日期字符串（yyyy年MM月dd日）
     * @param localDate 日期
     * @return 日期字符串
     */
    public static String getDateChinaStr(LocalDate localDate) {
        Assert.notNull(localDate, "localDate 参数不能为空");
        return localDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
    }
}
