package com.example.database.fanyumeta.server.tellhow;

import com.example.database.fanyumeta.server.Service;
import com.example.database.fanyumeta.server.gh.GHRequestMessage;
import com.example.database.fanyumeta.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送数据到泰豪
 */
@Data
public class ResponseMessage {

    public ResponseMessage(String ghReqId, Service service, Object data) {
        this.id = StringUtils.getUUID();
        this.ghReqId = ghReqId;
        this.service = service;
        this.data = data;
        this.user = new User();
    }

    public ResponseMessage(GHRequestMessage ghRequestMessage) {
        this(ghRequestMessage.getId(),
                ghRequestMessage.getService(),
                ghRequestMessage.getData());
    }

    /**
     * 请求 id，唯一标识
     */
    private String id;

    /**
     * 广哈请求 id
     */
    private String ghReqId;

    /**
     * 请求服务
     */
    private Service service;

    /**
     * 请求数据
     */
    private Object data;

    /**
     * 声纹用户信息
     */
    private User user;

    /**
     * 用户信息
     */
    @Data
    public class User implements Serializable {

        /**
         * 用户编号
         */
        private String id;

        /**
         * 用户姓名
         */
        private String name;

        /**
         * 匹配度
         */
        private String matchDegree;
    }
}
