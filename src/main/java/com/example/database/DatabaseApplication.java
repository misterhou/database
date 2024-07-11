package com.example.database;

import com.alibaba.fastjson.JSONObject;
import com.example.database.fanyumeta.utils.PicDataUtil;
import com.example.database.fanyumeta.utils.StringUtils;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MapperScan({"com.example.database.mapper", "com.example.database.fanyumeta.mapper"})
@SpringBootApplication
public class DatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseApplication.class, args);
//        testOgnl();
//        generateCacheFile();
//        testPattern();
//        testSegment();
    }

    private static void generateCacheFile() {
        try {
//            HardwareControlCommandUtil.generateRequestCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                    "RequestCommandCache");
//            HardwareControlCommandUtil.generateReceiveCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                    "ReceiveCommandCache");
//            HardwareControlCommandUtil.generateRequestReceiveCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                    "RequestReceiveCommandCache");
//            PicDataUtil.generatePicDataCacheFile("src/main/resources/开图清单.xlsx",
//                    "PicDataCache");
//            PicDataUtil.generateStationPicDataCacheFile("src/main/resources/厂站信息.xlsx",
//                    "SubstationDataCache");
            PicDataUtil.generateSourcePicDataCacheFile(Arrays.asList(
                    "src/main/resources/v_rt_dev_transfmwd.xlsx",   // 主变
                            "src/main/resources/v_rt_dev_breaker.xlsx", // 开关
                            "src/main/resources/v_rt_dev_busbar.xlsx",  // 母线
                            "src/main/resources/v_rt_dev_dmsbreaker.xlsx",  // 开关站-开关
                            "src/main/resources/v_rt_dev_dmsbs.xlsx",   // 开关站-母线
                            "src/main/resources/v_rt_dev_dmstr.xlsx"    // 开关站-变压器
                    ),
                    "SourceDataCache");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void testOgnl() {
        JSONObject responseData = JSONObject.parseObject("{\"tabData\":\"无\",\"jpgPath\":\"无\",\"id\":\"无\",\"results\":{\"answer\":\"回答内容\",\"docs\":[]}}");
        Map defaultContext = Ognl.createDefaultContext(responseData);
        try {
            Object value = Ognl.getValue("results1.answer1", responseData);
            System.out.println(value);
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testPattern() {
        String str = "35kV容城县王庄站";
        Pattern pattern = Pattern.compile("\\d+(kV|kv|KV)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String patterStr = matcher.group();
            System.out.println(patterStr);
        }
    }

    private static void testSegment() throws IOException {
        System.out.println(StringUtils.segment("打开剧村站10kV1B母线溯源图"));
    }
}
