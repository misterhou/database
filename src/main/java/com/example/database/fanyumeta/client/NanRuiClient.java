package com.example.database.fanyumeta.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.database.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 南瑞客户端
 */
@Component
@Slf4j
public class NanRuiClient {

    @Value("${fan-yu.nan-rui.service-addr}")
    private String serviceAddr;

    /**
     * 天气请求地址
     */
    private static final String WEATHER_URL = "/weather";

    /**
     * 重过载设备请求地址
     */
    private static final String HEAVY_DEVICE_URL = "/heavy-device";

    /**
     * 获取天气信息
     * @param cityName 城市名称
     * @return 天气信息
     */
    public String getWeather(String cityName) {
        String url = this.serviceAddr + WEATHER_URL + "/" + cityName;
        String weatherInfo = null;
        JSONObject weather = getDataNr(url);
        if (null != weather) {
            weatherInfo = "今天天气" + weather.getString("description") +
                    "，气温" + weather.getString("temperature") +
                    "度，风向风速" + weather.getString("wind");
        }
        return weatherInfo;
    }


    public String getHeavyDevice() {
        String url = this.serviceAddr + HEAVY_DEVICE_URL;
        JSONObject heavyDeviceResData = getDataNr(url);
        String result = null;
        if (null != heavyDeviceResData) {
            JSONArray data = heavyDeviceResData.getJSONArray("data");
            if (null != data && data.size() > 0) {
                int overCount = 0;
                int heavyCount = 0;
                List<String> heavyDeviceList = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    JSONObject heavyDevice = data.getJSONObject(i);
                    if (null != heavyDevice) {
                        String rateStr = heavyDevice.getString("rate");
                        String name = heavyDevice.getString("name");
                        if (StringUtils.hasText(rateStr)) {
                            Double rate = Double.valueOf(rateStr);
                            if (rate >= 100.0) {
                                overCount++;
                            } else if (rate >= 80) {
                                heavyCount++;
                            }
                        }
                        heavyDeviceList.add(name + "负载率为" + rateStr);
                    }
                }
                List<String> temp = new ArrayList<>();
                if (overCount > 0) {
                    temp.add("过载设备有" + overCount + "个");
                }
                if (heavyCount > 0) {
                    temp.add("重载设备有" + heavyCount + "个");
                }
                temp.addAll(heavyDeviceList);
                heavyDeviceList = temp;
                result = "当前" + String.join("，", heavyDeviceList);
            }
        }
        if(!StringUtils.hasText(result)) {
            result = "当前电网运行良好，没有重过载设备";
        }
        return result;
    }

    @Nullable
    private static JSONObject getDataNr(String url) {
        JSONObject resData = null;
        log.info("调用梵钰-南瑞接口：{}", url);
        try {
            String responseData = HttpClientUtil.sendGet(url, null);
            log.info("调用梵钰-南瑞接口：{}，响应结果：{}", url, responseData);
            resData = JSONObject.parseObject(responseData);
        } catch (Exception e) {
            log.error("通过纳瑞服务获取数据出错", e);
        }
        return resData;
    }
}
