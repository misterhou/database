package com.example.database;

import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import com.example.database.fanyumeta.utils.PicDataUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

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
//        testOgnl();
    }

//    public static void main(String[] args) throws Exception {
//        HardwareControlCommandUtil.generateRequestCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                "RequestCommandCache");
//        HardwareControlCommandUtil.generateReceiveCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                "ReceiveCommandCache");
//        HardwareControlCommandUtil.generateRequestReceiveCommandCacheFile("src/main/resources/realtime-public-100092-org.xlsx",
//                "RequestReceiveCommandCache");
//        PicDataUtil.generatePicDataCacheFile("src/main/resources/开图清单.xlsx",
//                "PicDataCache");
//        Pattern pattern = Pattern.compile("(28|二十八)度参观台东侧空调|参观台东侧空调(28|二十八)度");
//        Pattern pattern = Pattern.compile("(18|(十八))度参观台东侧空调|参观台东侧空调(18|十八)度");
//        if (pattern.matcher("二十八度参观台东侧空调").matches()) {
//            System.out.println("ok");
//        }
//    }

//    private static void testOgnl() {
//        JSONObject responseData = JSONObject.parseObject("{\"tabData\":\"无\",\"jpgPath\":\"无\",\"id\":\"无\",\"results\":{\"answer\":\"回答内容\",\"docs\":[]}}");
//        Map defaultContext = Ognl.createDefaultContext(responseData);
//        try {
//            Object value = Ognl.getValue("results1.answer1", responseData);
//            System.out.println(value);
//        } catch (OgnlException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
