package com.example.database.fanyumeta.utils;

import com.example.database.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 图片数据工具类
 */
@Slf4j
public class PicDataUtil {

    private static List<Map<String, String>> picDataCache = new ArrayList<>(256);

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
        String picName = null;
        for (Map<String, String> picData : picDataCache) {
            for (String key : picData.keySet()) {
                if (key.equals(id)) {
                    picName = picData.get(key);
                }
            }
        }
        return picName;
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
        log.info("图片数量：{}\n{}", data.size(), data);
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
}
