package com.example.database.fanyumeta.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IIOAdapter;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils extends org.springframework.util.StringUtils {

    /**
     * 初始化自定义词典
     */
    static {
        InputStream is = StringUtils.class.getResourceAsStream("/dictionary/custom/ext.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                CustomDictionary.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public static List<String> segment(String text) {
        List<Term> terms = HanLP.segment(text);
        return terms.stream().map(term -> term.word).collect(Collectors.toList());
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
