package com.example.database.fanyumeta.server.tellhow;

import com.example.database.fanyumeta.server.Service;
import com.example.database.fanyumeta.server.gh.GHRequestMessage;
import com.example.database.fanyumeta.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送数据到泰豪
 */
@Data
public class ResponseMessage {

    public ResponseMessage(String ghReqId, Service service, Object data, WindowSize windowSize) {
        this.id = StringUtils.getUUID();
        this.ghReqId = ghReqId;
        this.service = service;
        this.data = data;
        this.frmSize = windowSize;
        if (null != data) {
            this.result = true;
        } else {
            this.result = false;
        }
        this.timestamp = StringUtils.getTimestamp();
//        this.user = new User();
    }

    public ResponseMessage(GHRequestMessage ghRequestMessage) {
        this(ghRequestMessage.getId(),
                ghRequestMessage.getService(),
                ghRequestMessage.getData(), null);
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
     * 窗口大小
     */
    private WindowSize frmSize;

    /**
     * 菜单编号
     */
    private String menuCode;

    /**
     * 响应时间
     */
    private String timestamp;

    /**
     * 响应结果
     */
    private Boolean result;

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

    @Data
    @AllArgsConstructor
    public static class WindowSize implements Serializable {

        /**
         * 宽度
         */
        private String width;

        /**
         * 高度
         */
        private String height;
    }
}
