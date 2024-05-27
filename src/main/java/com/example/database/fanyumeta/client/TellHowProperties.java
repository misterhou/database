package com.example.database.fanyumeta.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("fan-yu.tell-how")
public class TellHowProperties {

    /**
     * 服务地址
     */
    private String serviceAddr;

    /**
     * 图片地址
     */
    private String picAddr;

    /**
     * 图片配置文件
     */
    private String picConfigFile;
}
