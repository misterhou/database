package com.example.database.utils;

public class TimeUtil {
    public static String getYear(String year){
        year=year.replace("一","1");
        year=year.replace("二","2");
        year=year.replace("三","3");
        year=year.replace("四","4");
        year=year.replace("五","5");
        year=year.replace("六","6");
        year=year.replace("七","7");
        year=year.replace("八","8");
        year=year.replace("九","9");
        year=year.replace("零","0");
        return year;
    }

    public static String getMonth(String month){
        month= String.valueOf(ArabicNumeralsUtil.zh2arbaNum(month));
        return month;
    }

    public static String getDay(String day){
        day= String.valueOf(ArabicNumeralsUtil.zh2arbaNum(day));
        return day;
    }
}
