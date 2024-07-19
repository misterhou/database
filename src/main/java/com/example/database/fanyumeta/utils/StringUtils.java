package com.example.database.fanyumeta.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IIOAdapter;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import org.springframework.util.Assert;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class StringUtils extends org.springframework.util.StringUtils {

//    /**
//     * 初始化自定义词典
//     */
//    static {
//        InputStream is = StringUtils.class.getResourceAsStream("/dictionary/custom/ext.txt");
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//        String line = null;
//        try {
//            while ((line = bufferedReader.readLine()) != null) {
//                CustomDictionary.add(line);
//            }
//            bufferedReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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

    /**
     * 正则表达式匹配
     *
     * @param reg 正则表达式
     * @param message 待匹配字符串
     * @return 是否匹配
     */
    public static boolean regexIsFind(String reg, String message) {
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(message).find();
    }

    /**
     * 分词
     *
     * @param text 文本
     * @return 分词列表
     */
    public static List<String> segment(String text) throws IOException {
//        List<Term> terms = HanLP.segment(text);
//        return terms.stream().map(term -> term.word).collect(Collectors.toList());
        List<String> keyword = new ArrayList<>();
        StringReader re = new StringReader(text.trim());
        IKSegmenter ik = new IKSegmenter(re,true);
        Lexeme lex;
        while((lex = ik.next())!=null){
            keyword.add(getNumberReg(lex.getLexemeText()));
        }
        return keyword;
    }

    public static String getPinyin(String text) {
        HanLP.Config.IOAdapter = new SpringbootResourceIOAdapter();
        HanLP.Config.PinyinDictionaryPath = "/dictionary/pinyin/pinyin.txt";
        List<Pinyin> pinyins = HanLP.convertToPinyinList(text);
        List<String> pinYinList = new ArrayList<>();
        for (int i = 0; i < pinyins.size(); i++) {
            Pinyin pinyin = pinyins.get(i);
            if (Pinyin.none5 == pinyin) {
                char[] charArray = text.toCharArray();
                pinYinList.add(String.valueOf(charArray[i]));
            } else {
                pinYinList.add(pinyin.getPinyinWithoutTone());
            }
        }
        return String.join("", pinYinList);
    }

    /**
     * 特殊字符处理
     *
     * @param text 待处理文本
     * @return 处理后的文本
     */
    public static String replaceSpecialSymbol(String text) {
        text = text.replaceAll("#1|1#", "1号");
        text = text.replaceAll("#2|2#", "2号");
        text = text.replaceAll("#3|3#", "3号");
        text = text.replaceAll("#4|4#", "4号");
        text = text.replaceAll("#5|5#", "5号");
        text = text.replaceAll("Ⅰ", "1");
        text = text.replaceAll("Ⅱ", "2");
        text = text.replaceAll("Ⅲ", "3");
        text = text.replaceAll("Ⅳ", "4");
        text = text.replaceAll("Ⅴ", "5");
        text = text.replace("Ⅵ" , "6");
        text = text.replace("Ⅶ" , "7");
        text = text.replace("Ⅷ" , "8");
        text = text.replace("Ⅸ" , "9");
        return text;
    }

    /**
     * 获取数字正则表达式
     *
     * @param text 待处理文本
     * @return 正则表达式
     */
    public static String getNumberReg(String text) {
        String reg = text;
        if (hasText(text)) {
            if (text.contains("一")) {
                reg = reg.replaceAll("一", "(1|一)");
            } else if (text.contains("二")) {
                reg = reg.replaceAll("二", "(2|二)");
            } else if (text.contains("三")) {
                reg = reg.replaceAll("三", "(3|三)");
            } else if (text.contains("四")) {
                reg = reg.replaceAll("四", "(4|四)");
            } else if (text.contains("五")) {
                reg = reg.replaceAll("五", "(5|五)");
            } else if (text.contains("六")) {
                reg = reg.replaceAll("六", "(6|六)");
            } else if (text.contains("七")) {
                reg = reg.replaceAll("七", "(7|七)");
            } else if (text.contains("八")) {
                reg = reg.replaceAll("八", "(8|八)");
            } else if (text.contains("九")) {
                reg = reg.replaceAll("九", "(9|九)");
            }
        }
        return reg;
    }

    /**
     * URL 编码
     *
     * @param text 待编码文本
     * @return 编码后的文本
     */
    public static String urlEncode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class SpringbootResourceIOAdapter implements IIOAdapter {
        @Override
        public InputStream open(String path) throws IOException {
            return SpringbootResourceIOAdapter.class.getResourceAsStream(path);
        }

        @Override
        public OutputStream create(String path) throws IOException {
            throw new IllegalArgumentException("不支持写入jar包资源路径" + path);
        }
    }
}
