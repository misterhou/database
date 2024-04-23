package com.example.database;

import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
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
    }

//    public static void main(String[] args) throws IOException {
//        HardwareControlCommandUtil.generateRequestCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                "RequestCommandCache");
//        HardwareControlCommandUtil.generateReceiveCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                "ReceiveCommandCache");
//        HardwareControlCommandUtil.generateRequestReceiveCommandCacheFile("src/main/resources/中控指令集.xlsx",
//                "RequestReceiveCommandCache");
//        Pattern pattern = Pattern.compile("(28|二十八)度参观台东侧空调|参观台东侧空调(28|二十八)度");
//        Pattern pattern = Pattern.compile("(18|(十八))度参观台东侧空调|参观台东侧空调(18|十八)度");
//        if (pattern.matcher("二十八度参观台东侧空调").matches()) {
//            System.out.println("ok");
//        }
//    }

}
