package com.example.database.fanyumeta.controller;

import com.example.database.fanyumeta.enums.PicType;
import com.example.database.fanyumeta.server.TellHowServer;
import com.example.database.fanyumeta.utils.PicDataUtil;
import com.example.database.fanyumeta.vo.PicOpenReq;
import com.example.database.fanyumeta.vo.PicOpenRes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开图第三方调用接口（目前调用方：海颐）
 */
@RestController
@RequestMapping("/pic")
public class PicController {

    /**
     * 开图（仅推送开图消息到泰豪，不与数智人交互）
     *
     * @param picOpenReq 开图请求参数
     * @return 开图结果
     */
    @PostMapping("/open")
    public PicOpenRes open(@RequestBody PicOpenReq picOpenReq) {
        PicOpenRes picOpenRes = new PicOpenRes();
        String picName = picOpenReq.getPicName();
        PicType picType = picOpenReq.getPicType();

        if (StringUtils.isBlank(picName)) {
            picOpenRes = PicOpenRes.picNameIsBlank();
        } else if (picType == null) {
            picOpenRes = PicOpenRes.picTypeIsBlank();
        } else if (PicType.CONTACT == picType) {
            String substationRtKeyId = PicDataUtil.getSubstationRtKeyId(picName);
            if (StringUtils.isNotBlank(substationRtKeyId)) {
                TellHowServer.noticeClient(substationRtKeyId, com.example.database.fanyumeta.server.tellhow.PicType.CONTACT, null);
                picOpenRes = PicOpenRes.success();
            } else {
                picOpenRes = PicOpenRes.picNameNotFound();
            }
        } else if (PicType.SOURCE == picType) {
            String substationRtKeyId = PicDataUtil.getSourcePicRtKeyId(picName);
            if (StringUtils.isNotBlank(substationRtKeyId)) {
                TellHowServer.noticeClient(substationRtKeyId, com.example.database.fanyumeta.server.tellhow.PicType.SOURCE, null);
                picOpenRes = PicOpenRes.success();
            } else {
                picOpenRes = PicOpenRes.picNameNotFound();
            }
        } else {
            picOpenRes = PicOpenRes.picTypeNotFound();
        }
        return picOpenRes;
    }
}
