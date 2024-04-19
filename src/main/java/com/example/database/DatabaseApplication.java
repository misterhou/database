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

}
