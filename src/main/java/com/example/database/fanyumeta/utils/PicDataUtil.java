package com.example.database.fanyumeta.utils;

import com.example.database.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 图片数据工具类
 */
@Slf4j
public class PicDataUtil {

    /**
     * 图片数据缓存（南瑞开发的接线图/间隔图）
     */
    private static List<Map<String, String>> picDataCache = new ArrayList<>(256);

    /**
     * 厂站图片数据缓存（东方电子开发的厂站联络图）
     */
    private static Map<String, String> substationDataCache = new TreeMap<>(new DeviceNameComparator());

    /**
     * 溯源图数据缓存
     */
    private static Map<String, String> sourcePicDataCache = new TreeMap<>(new DeviceNameComparator());

    /**
     * 厂站表 id
     */
    private static final String SUBSTATION_TABLE_ID = "112";

    /**
     * 初始化图片数据缓存
     * @param picDataCacheFile 图片数据缓存文件
     */
    public static void initPicData(String picDataCacheFile) {
        picDataCache.clear();
        try {
            picDataCache.addAll(loadPicDataCacheFile(picDataCacheFile));
        } catch (Exception e) {
            log.error("加载图片数据缓存文件出错", e);
        }
    }

    /**
     * 初始化厂站图片数据缓存
     *
     * @param substationDataFile 图片数据缓存文件
     */
    public static void initSubstationData(String substationDataFile) {
        try {
            loadExcelData2Map(substationDataFile, substationDataCache);
        } catch (Exception e) {
            log.error("加载厂站图片数据缓存文件出错", e);
        }
    }

    /**
     * 初始化溯源图数据缓存
     *
     * @param transformerDataFile 数据缓存文件
     */
    public static void initSourcePicData(String transformerDataFile) {
        sourcePicDataCache.clear();
        try {
            loadExcelData2Map(transformerDataFile, sourcePicDataCache);
        } catch (Exception e) {
            log.error("加载溯源图缓存文件出错", e);
        }
    }

//    /**
//     * 获取图片名称
//     * @param message 待匹配的消息
//     * @return 图片名称
//     */
//    public static String getPicName(String message) {
//        String picName = null;
//        message = message.replaceAll("千伏", "kV");
//        for (Map<String, String> picData : picDataCache) {
//            for (String key : picData.keySet()) {
//                String chinesePart = key.replaceAll("[^\u4e00-\u9fa5]", "");
//                if (isFind(chinesePart, key.toString())) {
//                    String numberPart = key.replaceAll("[^0-9kV]", "");
//                    if (StringUtils.hasText(numberPart)) {
//                        String number = numberPart.replaceAll("kV", "");
//                        if (StringUtils.hasText(number)) {
//                            String numberChinese = NumberUtils.convert2Chinese(Integer.parseInt(number));
//                            numberPart = numberPart.replaceAll(number, numberChinese);
//                            if (isFind(numberPart, message)) {
//                                picName = picData.get(key);
//                            }
//                        } else {
//                            picName = picData.get(key);
//                        }
//                    } else {
//                        picName = picData.get(key);
//                    }
//                }
//            }
//        }
//        return picName;
//    }

    /**
     * 获取图片名称
     * @param id 图片 id
     * @return 图片名称
     */
    public static String getPicName(String id) {
        return getValue(id, picDataCache);
    }

    /**
     * 获取厂站图片 id
     *
     * @param message 厂站名称
     * @return 厂站图片 id
     */
    public static String getSubstationRtKeyId(String message) {
        String rtKeyId = null;
        String substationId = getSubstationId(message);
        if (StringUtils.hasText(substationId)) {
            rtKeyId = SUBSTATION_TABLE_ID + "_" + substationId;
        }
        return rtKeyId;
    }

    /**
     * 获取联络图 id
     *
     * @param text 开图文本
     * @return 联络图 id
     */
    public static String getSubstationId(String text) {
        String substationId = null;
        text = text.replaceAll("打开", "");
        text = text.replaceAll("联络图", "");
        for (String key : substationDataCache.keySet()) {
            Pattern namePatter = Pattern.compile(key);
            Matcher matcher = namePatter.matcher(text);
            if (matcher.find()) {
                log.info("【联络图匹配结果】开图指令：{}, 匹配到的厂站：{}", text, key);
                substationId = substationDataCache.get(key);
                if (StringUtils.hasText(substationId)) {
                    // 去掉前 4 位
                    substationId = substationId.substring(4);
                }
                break;
            }
        }
        return substationId;
    }

    /**
     * 获取溯源图 id
     * @param text 开图文本
     * @return 溯源图 id
     */
    public static String getSourcePicRtKeyId(String text) {
        String rtKeyId = null;
        text = text.replaceAll("打开", "");
        text = text.replaceAll("溯源图", "");
        List<String> keywordList = null;
        try {
            keywordList = StringUtils.segment(text);
        } catch (IOException e) {
            log.error("分词出错", e);
        }
        for (String key : sourcePicDataCache.keySet()) {
            int count = 0;
            for (String keyword : keywordList) {
                if (StringUtils.regexIsFind(keyword, key.toLowerCase())) {
                    count++;
                }
            }
            BigDecimal score = BigDecimal.valueOf(count).divide(
                    BigDecimal.valueOf(keywordList.size()),
                    4, RoundingMode.HALF_UP);
            if (log.isDebugEnabled()) {
                log.debug("匹配度：{}", score);
            }
            if (score.doubleValue() > 0.8) {
                log.info("【溯源图匹配结果】开图指令：{}, 匹配到的设备：{}, 匹配度：{}", text, key, score);
                rtKeyId = sourcePicDataCache.get(key);
                if (StringUtils.hasText(rtKeyId)) {
                    // 去掉前 4 位
                    rtKeyId = rtKeyId.substring(0, 4) + "_" + rtKeyId.substring(4);
                }
                if (StringUtils.hasText(rtKeyId)) {
                    // 溯源图需补 4 个 0
                    rtKeyId = rtKeyId + "0000";
                }
                if (StringUtils.hasText(rtKeyId)) {
                    // 去掉开头的 0
                    rtKeyId = rtKeyId.replaceAll("^0", "");
                }
                break;
            }
        }
        return rtKeyId;
    }

    /**
     * 生成图片数据缓存文件
     * @param excelFile excel 文件
     * @param cacheFile 缓存文件
     * @throws Exception 当读取 excel 数据报错时，会抛出此异常
     */
    public static void generatePicDataCacheFile(String excelFile, String cacheFile) throws Exception {
        List<Map<String, String>> data = ExcelUtils.getData(excelFile, 0, "图形名称", -3);
        writeObject2File(data, cacheFile);
    }

    /**
     * 生成厂站图片数据缓存文件
     * @param excelFile excel 文件
     * @param cacheFile 缓存文件
     * @throws Exception 当读取 excel 数据报错时，会抛出此异常
     */
    public static void generateStationPicDataCacheFile(String excelFile, String cacheFile) throws Exception {
        List<Map<String, String>> data = ExcelUtils.getData(excelFile, 0, "id", 1);
        writeObject2File(data, cacheFile);
    }

    public static void generateSourcePicDataCacheFile(List<String> excelFileList, String cacheFile) throws Exception {
        List<Map<String, String>> data = new ArrayList<>(20000);
        for (String excelFile : excelFileList) {
            List<Map<String, String>> subData = ExcelUtils.getData2(excelFile, 0,
                    "id", Arrays.asList(3, 2, 1));
            data.addAll(subData);
        }
        writeObject2File(data, cacheFile);
    }

    /**
     * 将对象写入文件
     *
     * @param obj 待写入的对象
     * @param file 待写入的文件
     * @throws IOException 将对象写入文件报错时，会抛出此异常
     */
    private static void writeObject2File(Object obj, String file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
        outputStream.writeObject(obj);
        outputStream.close();
    }

    /**
     * 加载图片文件
     *
     * @param commandCacheFile 缓存文件
     * @return 缓存 map
     * @throws IOException 配置文件读取出错，会抛出此异常
     * @throws ClassNotFoundException 配置文件数据有问题，会抛出此异常
     */
    public static List<Map<String, String>> loadPicDataCacheFile(String commandCacheFile) throws IOException,
            ClassNotFoundException {
        List<Map<String, String>> data = null;
        log.info("开始加载图片缓存文件：{}", commandCacheFile);
        ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(Paths.get(commandCacheFile)));
        data = (List<Map<String, String>>) inputStream.readObject();
        if (log.isDebugEnabled()) {
            log.debug("图片缓存文件数据：{}", data);
        }
        log.info("图片数量：{}", data.size());
        return data;
    }

    /**
     * 判断是否匹配正则表达式
     *
     * @param reg 正则表达式
     * @param message 待匹配字符串
     * @return 匹配结果
     */
    private static boolean isFind(String reg, String message) {
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(message).find();
    }

    /**
     * 获取图片名称
     * @param id 图片 id
     * @param dataCache 图片缓存数据
     * @return 图片名称
     */
    private static String getValue(String id, List<Map<String, String>> dataCache) {
        String picName = null;
        for (Map<String, String> picData : dataCache) {
            for (String key : picData.keySet()) {
                if (key.equals(id)) {
                    picName = picData.get(key);
                }
            }
        }
        return picName;
    }

    /**
     * 设备名称逆序比较器
     */
    private static class DeviceNameComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            StringBuilder o1Builder = new StringBuilder(o1);
            StringBuilder o2Builder = new StringBuilder(o2);
            String o1Reverse = o1Builder.reverse().toString();
            String o2Reverse = o2Builder.reverse().toString();
            return o2Reverse.compareTo(o1Reverse);
        }
    }

    /**
     * 加载 excel 数据
     * @param excelFile excel 文件
     * @param dataCache 数据缓存
     * @throws Exception 加载数据出错时，会抛出此异常
     */
    private static void loadExcelData2Map(String excelFile, Map<String, String> dataCache) throws Exception {
        dataCache.clear();
        List<Map<String, String>> transformerExcelData = new ArrayList<>(256);
        transformerExcelData.addAll(loadPicDataCacheFile(excelFile));
        for (Map<String, String> picData : transformerExcelData) {
            for (String key : picData.keySet()) {
                dataCache.put(key, picData.get(key));
            }
        }
    }
}
