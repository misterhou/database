package com.example.database.fanyumeta.server.tellhow;

import com.example.database.fanyumeta.server.ServiceType;
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

    public ResponseMessage(String ghReqId, ServiceType serviceType, Object data, WindowSize windowSize) {
        this.id = StringUtils.getUUID();
        this.ghReqId = ghReqId;
        this.service = serviceType;
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
                ghRequestMessage.getServiceType(),
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
    private ServiceType service;

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
    private TellHowMenu menuCode;

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

    public String getMenuCode() {
        return null == this.menuCode ? null : this.menuCode.code;
    }

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

    /**
     * 泰豪菜单枚举
     */
    public enum TellHowMenu {

        /**
         * 值班人员信息
         */
        DUTY_PERSONNEL_INFO("zbryxx"),

        /**
         * 雄安总加负荷曲线
         */
        TOTAL_LOAD_CURVE("xazjfhqx"),

        /**
         * 电网分片区负荷曲线安新县dwfpqfhqx_01
         */
        AN_XIN_LOAD_CURVE("dwfpqfhqx_01"),

        /**
         * 电网分片区负荷曲线容城县dwfpqfhqx_02
         */
        RONG_CHENG_LOAD_CURVE("dwfpqfhqx_02"),

        /**
         * 电网分片区负荷曲线雄县县城dwfpqfhqx_03
         */
        XIONG_XIAN_LOAD_CURVE("dwfpqfhqx_03"),

        /**
         * 电网分片区负荷曲线沧州片区dwfpqfhqx_04
         */
        CANG_ZHOU_LOAD_CURVE("dwfpqfhqx_04"),

        /**
         * 电网分片区负荷曲线容东片区dwfpqfhqx_05
         */
        RONG_DONG_LOAD_CURVE("dwfpqfhqx_05"),

        /**
         * 电网分片区负荷曲线容西片区dwfpqfhqx_06
         */
        RONG_XI_LOAD_CURVE("dwfpqfhqx_06"),

        /**
         * 电网分片区负荷曲线雄东片区dwfpqfhqx_07
         */
        XIONG_DONG_LOAD_CURVE("dwfpqfhqx_07"),

        /**
         * 电网分片区负荷曲线启动区dwfpqfhqx_08
         */
        QI_DONG_LOAD_CURVE("dwfpqfhqx_08"),

        /**
         * 电网分片区负荷曲线目标电网dwfpqfhqx_09
         */
        MU_BIAO_LOAD_CURVE("dwfpqfhqx_09"),

        /**
         * 电网分片区负荷曲线雄县(含沧州)dwfpqfhqx_10
         */
        XIONG_XIAN_CANG_ZHOU_LOAD_CURVE("dwfpqfhqx_10"),


        /**
         * 主变负载率
         */
        TRANS_LOAD_RATE("zbfzlb"),

        /**
         * 线路负载率
         */
        LINE_LOAD_RATE("zbfzlv"),

        /**
         * N-1 明细
         */
        NUM_MINUS_ONE("n-1mx"),

        /**
         * 负荷走势预测
         */
        FORECAST_LOAD_TREND("fhzsyc_zw");

        private String code;

        TellHowMenu(String code) {
            this.code = code;
        }
    }
}
