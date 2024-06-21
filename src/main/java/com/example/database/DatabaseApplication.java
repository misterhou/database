package com.example.database;

import com.alibaba.fastjson.JSONObject;
import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import com.example.database.fanyumeta.utils.PicDataUtil;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MapperScan("com.example.database.mapper")
@SpringBootApplication
public class DatabaseApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(DatabaseApplication.class, args);
        Environment environment = ctx.getEnvironment();
        HardwareControlCommandUtil.initCache(
                environment.getProperty("fan-yu.hardware-control.request-command-config-file"),
                environment.getProperty("fan-yu.hardware-control.receive-command-config-file"));
        PicDataUtil.initPicData(environment.getProperty("fan-yu.tell-how.pic-config-file"));
        PicDataUtil.initSubstationData(environment.getProperty("fan-yu.tell-how.substation-config-file"));
//        testOgnl();
//        generateCacheFile();
//        testPattern();
    }

    private static void generateCacheFile() {
        try {
            HardwareControlCommandUtil.generateRequestCommandCacheFile("src/main/resources/中控指令集.xlsx",
                    "RequestCommandCache");
            HardwareControlCommandUtil.generateReceiveCommandCacheFile("src/main/resources/中控指令集.xlsx",
                    "ReceiveCommandCache");
            HardwareControlCommandUtil.generateRequestReceiveCommandCacheFile("src/main/resources/中控指令集.xlsx",
                    "RequestReceiveCommandCache");
            PicDataUtil.generatePicDataCacheFile("src/main/resources/开图清单.xlsx",
                    "PicDataCache");
            PicDataUtil.generateStationPicDataCacheFile("src/main/resources/厂站信息.xlsx",
                    "SubstationDataCache");
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
}
