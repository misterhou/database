package com.example.database.utils;

public class GeneratrixUtil {
    public static String getText(String text){
        text=text.replace("一","1");
        text=text.replace("二","2");
        text=text.replace("三","3");
        text=text.replace("四","4");
        text=text.replace("五","5");
        text=text.replace("六","6");
        text=text.replace("七","7");
        text=text.replace("八","8");
        text=text.replace("九","9");
        text=text.replace("零","0");
        return text;
    }
}
