package com.example.database.fanyumeta.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

/**
 * json 工具类
 *
 */
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 将对象转换为 json 字符串
     *
     * @param object 对象
     * @return json 字符串
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 json 字符串转换为对象
     *
     * @param json json 字符串
     * @param clazz 转换为的对象类型
     * @return 对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 json 字符串转换为 json 对象
     *
     * @param json json 字符串
     * @return json 对象
     */
    public static JsonNode parseObject(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 注册子类型
     *
     * @param clazz 子类型
     * @param type 类型
     */
    public static void registerSubType(Class<?> clazz, String type) {
        objectMapper.registerSubtypes(new NamedType(clazz, type));
    }

}
