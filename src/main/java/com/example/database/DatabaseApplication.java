package com.example.database;

import com.alibaba.fastjson.JSONObject;
import com.example.database.fanyumeta.component.DBCheck;
import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import com.example.database.fanyumeta.utils.PicDataUtil;
import com.example.database.fanyumeta.utils.StringUtils;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MapperScan({"com.example.database.mapper", "com.example.database.fanyumeta.mapper"})
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
        PicDataUtil.initSourcePicData(environment.getProperty("fan-yu.tell-how.source-config-file"));
        DBCheck dbCheck = ctx.getBean(DBCheck.class);
        dbCheck.checkTableExist();
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
                    "src/main/resources/v_rt_dev_transfmwd.xlsx",
                            "src/main/resources/v_rt_dev_dmsbreaker.xlsx"),
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
