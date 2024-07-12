package com.example.database.fanyumeta.comparator;


import java.math.BigDecimal;
import java.util.Comparator;

/**
 * 数字比较器（倒序）
 */
public class NumberDescComparator implements Comparator<Number> {

    @Override
    public int compare(Number o1, Number o2) {
        if (o1 == null) {
            o1 = 0;
        }
        if (o2 == null) {
            o2 = 0;
        }
        BigDecimal bd1 = new BigDecimal(o1.toString());
        BigDecimal bd2 = new BigDecimal(o2.toString());
        return bd2.compareTo(bd1);
    }
}
