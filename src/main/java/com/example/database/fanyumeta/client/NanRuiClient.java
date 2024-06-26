package com.example.database.fanyumeta.client;

import com.alibaba.fastjson.JSONObject;
import com.example.database.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 南瑞客户端
 */
@Component
@Slf4j
public class NanRuiClient {

    @Value("${fan-yu.nan-rui.service-addr}")
    private String serviceAddr;

    /**
     * 天气路径
     */
    private static final String WEATHER_URL = "/weather";

    /**
     * 获取天气信息
     * @param cityName 城市名称
     * @return 天气信息
     */
    public String getWeather(String cityName) {
        String url = this.serviceAddr + WEATHER_URL + "/" + cityName;
        String weatherInfo = null;
        try {
            log.info("调用梵钰-南瑞接口：{}", url);
            String responseData = HttpClientUtil.sendGet(url, null);
            log.info("调用梵钰-南瑞接口：{}，响应结果：{}", url, responseData);
            JSONObject weather = JSONObject.parseObject(responseData);
            weatherInfo = "今天天气" + weather.getString("description") +
                    "，温度" + weather.getString("temperature") +
                    "度，风级" + weather.getString("wind");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherInfo;
    }
}
