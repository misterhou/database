package com.example.database.fanyumeta.vo;

import com.example.database.fanyumeta.enums.PicType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.IOException;

/**
 * 第三方开图请求参数
 */
@Data
public class PicOpenReq {

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片类型
     */
    @JsonDeserialize(using = PicTypeDeserializer.class)
    private PicType picType;

    public static class PicTypeDeserializer extends JsonDeserializer<PicType> {
        @Override
        public PicType deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            PicType picType = null;
            String text = p.getText();
            if (text != null && !text.trim().isEmpty()) {
                switch (text) {
                    case "1":
                        picType = PicType.CONTACT;
                        break;
                    case "2":
                        picType = PicType.SOURCE;
                        break;
                    default:
                        picType = PicType.TYPE_ERROR;
                }
            }
            return picType;
        }
    }
}
