package com.example.database.utils;


import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 正则表达式相关的方法
 * 23.4.24
 *
 * @author amily
 */
public class ZenzeUtils {

    /**
     * 生成随机整数
     * */
    public static Integer haveSj() {
        Random rand = new Random();

        // 生成一个随机整数（包括0）
        return rand.nextInt(100);
        //System.out.println("随机整数: " + randomInt);
    }


    /**
     * 只获取一段字符串中的数字
     */
    public static Integer havHaiba(String ha) {
        if (null != ha) {
            String s = ha.replaceAll("\\D+", "");
            if (null != s && !"".equals(s)) {
                return Integer.valueOf(s);
            } else {return null;}
        }else {return null;}
    }

    public static Double havHaibaD(String ha) {
        if (null != ha) {
            String s = ha.replaceAll("\\D+", "");
            if (null != s && !"".equals(s)) {
                return Double.valueOf(s);
            } else {return null;}
        }
        return null;
    }

    public static String havHaibaS(String ha) {
        if (null != ha) {
            String s = ha.replaceAll("\\D+", "");
            return s;
        }
        return null;
    }

    /**
     * 取一个字符串中某个字段之后的第一个数字
     *
     * @param field 某个字段
     * @param input 字符串
     */
    public static String havTheFirst(String field, String input) {
        // 使用正则表达式模式匹配字段之后的第一个数字
        String pattern = field + "(\\d+)";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(input);

        // 查找匹配的部分
        if (matcher.find()) {
            // 获取匹配的结果
            String number = matcher.group(1);
            return number;
        } else {
            return "";
        }
    }

    /**
     * 获取某个字符后面的字符串
     *
     * @param searchString 某个字符
     */
    public static String haveFollowing(String input, String searchString) {

        Pattern pattern = Pattern.compile(Pattern.quote(searchString) + "(.*?)$"); // 正则表达式模式
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String charactersAfterSearchString = matcher.group(1); // 获取匹配的字符
            System.out.println("在 \"" + searchString + "\" 后面的字符是: " + charactersAfterSearchString);
            return charactersAfterSearchString;
        } else {
            System.out.println("未找到 \"" + searchString + "\" 后面的字符");
            return null;
        }
    }

    /**
     * 判断是固化规范书还是自编规范书
     *
     * @param g 固化id
     * @return 1固化 2自编
     */
    public static Integer ghOrZh(String g) {
        //9和G开头的都是固化规范书
        return g.startsWith("9") || g.startsWith("G") ? 1 : 2;
    }

    /**
     * 拿到一个字符串中最后一个数字
     *
     * @param str 字符串
     */
    public static Integer lastNum(String str) {
        Integer i = null;
        if (str != null && str.length() > 0) {
            String lastChar = str.substring(str.length() - 1);
            System.out.println(lastChar);
            if (isNumberString(lastChar)) {
                i = Integer.valueOf(lastChar);
            }
            return i;
        } else {
            System.out.println("String is empty or null!");
            return i;
        }
    }

    /**
     * 获取A之前的第一组数字
     *
     * @param a 传入“A”
     */
    public static String haveABefore(String input, String a) {
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?+(?=" + a + ")");
//        String zz = "\\d+(\\.\\d+)?".concat(a);
//        Pattern pattern = Pattern.compile(zz); // 正则表达式模式
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String firstGroup = matcher.group(); // 获取第一组匹配的数字
            System.out.println("第一组数字是：" + firstGroup);
            return firstGroup;
        } else {
            System.out.println("未找到匹配的数字和'A'");
            return null;
        }
    }

    /**
     * 获取所有“A"之前的数字
     */
    public static Set<String> haveABeforeAll(String input, String a) {
        Set<String> re = new HashSet<>();
        String z = "\\d+(?=" + a.concat(")");
        Pattern pattern = Pattern.compile(z); // 正则表达式模式
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String number = matcher.group(); // 获取匹配的数字
            System.out.println("找到数字：" + number);
            re.add(number);
        }
        return re;
    }

    /**
     * 获取字符串括号中的字符串
     *
     * @param str 字符串
     */
    public static Set<String> bracket(String str) {
        Set<String> set = new HashSet<>();
        String re = "";
        str = str.replace("（", "(");
        str = str.replace("）", ")");
        System.out.println("str" + str);
        //Pattern p1 = Pattern.compile("\\((.*?)\\)"); // 匹配括号中的内容
        Pattern p1 = Pattern.compile("(?<=\\().*?(?=\\))");
        Matcher m1 = p1.matcher(str);
        while (m1.find()) {
            set.add(m1.group());
        }

        return set;
    }

    /**
     * 将给定字符串中的字母替换为空
     *
     * @param content
     * @return
     */
    public static String replaceNumberWithRe(String content) {
        String rex = "[A-Za-z]*";
        Pattern pattern = Pattern.compile(rex);
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("");
    }

    /**
     * 获取start 和end 中间的数字
     */
    public static String haveBetween(String input, String start, String end) {
        String re = null;
        String pattern = "(?<=" + start + ")\\d+(?=" + end + ")";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(input);

        if (m.find()) {
            String result = m.group();
            System.out.println(result);
            re = result;
        } else {
            System.out.println("未找到匹配的数字");
        }
        return re;
    }

    /**
     * 验证固化id字符串长度
     *
     * @param s 固化id
     * @return true固化id长度正确 flase 长度错误
     */
    public static String curedIdLength(Set<String> s) {
        String sz = "";
        //String s1 = "G00K-500150312-00014";

        //int length = s1.length();
        Iterator<String> iterator = s.iterator();
        while (iterator.hasNext()) {
           /* if (length == iterator.next().length()) {
                sz = iterator.next();
            }*/
            String next = iterator.next();
            if (next.split("-").length == 3) {
                sz = next;
            }
        }
        return sz;
    }

    /**
     * 获取一个字符串中所有的中文
     */
    public static String getChinese(String str) {
        String regex = "[^\\u4e00-\\u9fa5]+";
        str = str.replaceAll(regex, "");
        System.out.println(str);
        return str;
    }

    /**
     * 获取一个字符串中所有的数字
     */
    public static List<String> getFigure(String input) {
        List<String> re = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            System.out.println("Found number: " + matcher.group());
            re.add(matcher.group());
        }
        return re;
    }

    /**
     * 适用于 jdk 1.8及以下，统计List集合中每个元素出现的次数
     * 例如frequencyOfListElements(["111","111","222"])
     * ->
     * 则返回Map {"111"=2,"222"=1}
     *
     * @param items
     * @return Map<String, Integer>
     * @author wuqx
     */
    public static Map<String, Integer> frequencyOfListW(List<String> items) {
        Map<String, Integer> map = new HashMap<>();
        if (items == null || items.size() == 0) {
            return map;
        }
        for (String k : items) {
            Integer count = map.get(k);
            map.put(k, (count == null) ? 1 : count + 1);
        }
        return map;
    }

    /**
     * 适用于 jdk 1.8及以上， 统计List集合中每个元素出现的次数
     * 例如frequencyOfListElements(["111","111","222","333"])
     * ->
     * 则返回Map {111=2, 222=1, 333=1}
     *
     * @param falcons
     * @return Map<String, Integer>
     */
    public static Map<String, Long> frequencyOfListQ(List<String> falcons) {
        if (CollectionUtils.isEmpty(falcons)) {
            return new HashMap<>();
        }
        return falcons.stream().collect(Collectors.groupingBy(k -> k, Collectors.counting()));
    }

    /**
     * 校验一个字符串中是否有汉字
     * 有ture
     */
    public static boolean containsChinese(String str) {
        String pattern = "[\u4e00-\u9fa5]";
        return Pattern.matches(".*" + pattern + ".*", str);
    }

    /**
     * 校验一个字符串是否结尾是数字
     */
    public static boolean endsWithChinese(String str2) {
        boolean endsWithChinese2 = Pattern.matches(".*[\u4e00-\u9fa5]$", str2);
        return endsWithChinese2;
    }
    /**
     * 校验一个字符串是否结尾是字母
     * */
    public static boolean endWithEnglish(String str) {
        return Pattern.matches(".*[a-zA-Z]$", str);
    }
    /**
     * 校验一个字符传中是否只包含数字
     */
    public static Boolean ifAllNum(String str) {

        boolean isNumeric = str.matches("\\d+"); // 使用正则表达式校验字符串是否只包含数字
        return isNumeric;
    }

    //正则表达式判断是否是数字字符串（可判断正数，负数和小数）
    public static boolean isNumberString(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是不是以字母和数字为结尾的
     * 是返回true
     */
    public static boolean endsWithLetterOrNumber(String str) {
        String regex = "[A-Za-z0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 去掉一个字符串中括号以及括号中的字符
     *
     * @Date 23.7.13
     */
    public static String removeBracket(String str) {
        str = str.replace("（", "(");
        str = str.replace("）", ")");
        String result = str.replaceAll("\\([^()]*\\)", "");
        return result;
    }

    /**
     * 去掉末尾的字母，如果这个字符串中只有字母，就不要去了
     */
    public static String removeLastZm(String value) {
        String result = value.replaceAll("[a-zA-Z]+$", "");
        if (value.matches("[a-zA-Z]+")) {
            result = value;
        }
        return result;
    }

    public static String getStr(String text, String patternString) {
        // 编译正则表达式到Pattern对象
        Pattern pattern = Pattern.compile(patternString);
        // 用Pattern对象创建一个Matcher对象
        Matcher matcher = pattern.matcher(text);
        // 使用Matcher对象的find方法来查找匹配的内容
        if (matcher.find()) {
            // 输出匹配的内容
            return matcher.group();
        } else {
            return null;
        }
    }


}
