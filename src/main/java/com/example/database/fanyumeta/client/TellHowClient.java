package com.example.database.fanyumeta.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.database.fanyumeta.client.vo.TellHowVO;
import com.example.database.fanyumeta.utils.StringUtils;
import com.example.database.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(TellHowProperties.class)
public class TellHowClient {

    private final TellHowProperties tellHowProperties;

    public TellHowClient(TellHowProperties tellHowProperties) {
        this.tellHowProperties = tellHowProperties;
    }

    /**
     * 获取当值人员
     * @param dutyDate 值班日期
     * @param dutyOrder 值次名称（一值、二值、三值、白班、夜班）
     * @return
     */
    public TellHowVO dutyPersonnelInfo(LocalDate dutyDate, String dutyOrder) {
        /*
            http://192.168.110.87:9875/admin/data/dutyPersonnelInfo?dateTime=2024-05-01%2000:00:00&dutyOrderName=%E4%B8%80%E5%80%BC&actionType=3
            1. actionType（必填）: 动作类型
                1：文字的结果，框里面显示，梵钰播报，动作挥中间
                2：数据需要组织用列表或者折线图表，梵钰组织界面梵钰播报，调开图链接，挥右边
                3：数据本身大屏有的，泰豪前端做下高亮显示，梵钰播报。按实际方位挥
                4：文档和大百科，梵钰组织界面调开图链接，梵钰播报，挥右边
                6：开接线图时挥左边在电网接线图与数智人中间
            2. dateTime（非必填）：日期时间 （格式：2024-05-01 00:00:00）
            3. dutyOrderName（非必填）：值次名称
            （一值、二值、三值、白班、夜班）

            （dateTime与dutyOrderName必须同时为空或同时不为空）

            返回值：
            resData：指标数据
            personnelName：姓名
            personnelIdentity：身份
            personnelPhoto：照片ID

            actionData：动作数据
            poseId：动作序号 （左侧挥手——1，左上侧挥手——2，右侧挥手——3，中间挥手——4，打电话——5）
            actionType：动作类型（将入参中的actionType直接返回）
         */
        TellHowVO tellHowVO = new TellHowVO();
        String result = null;
        String noticeDate = null;
        if (dutyDate == null) {
            dutyDate = LocalDate.now();
        }
        String dutyDateStr = StringUtils.getDateStr(dutyDate) + " 00:00:00";
        noticeDate = StringUtils.getDateChinaStr(dutyDate);
        String url = this.tellHowProperties.getServiceAddr() + "/admin/data/dutyPersonnelInfo";
        Map<String, String> params = new HashMap<>();
        params.put("dateTime", dutyDateStr);
        params.put("dutyOrderName", dutyOrder);
        params.put("actionType", "3");
        log.info("调用泰豪接口：{}，请求参数：{}", url, params);
        try {
            String reponseData = HttpClientUtil.sendGet(url , params);
            log.info("调用泰豪接口：{}，请求参数：{}，响应结果：{}", url, params, reponseData);
            JSONObject res = JSONObject.parseObject(reponseData);
            if (res.getIntValue("code") == 0) {
                JSONObject data = res.getJSONObject("data");
                if (data != null) {
                    JSONArray dutyPersons = data.getJSONArray("resData");
                    if (dutyPersons != null && dutyPersons.size() > 0) {
                        StringBuilder dutyInfo = new StringBuilder();
                        dutyInfo.append(noticeDate).append(dutyOrder).append("值班人员为：");
                        for (int i = 0; i < dutyPersons.size(); i++) {
                            String personnelName = dutyPersons.getJSONObject(i).getString("personnelName");
                            dutyInfo.append(personnelName).append(",");
                        }
                        tellHowVO.setVoiceContent(dutyInfo.substring(0, dutyInfo.length() - 1));
                        JSONObject actionData = data.getJSONObject("actionData");
                        if (actionData != null) {
                            String poseId = actionData.getString("poseId");
                            if (StringUtils.hasText(poseId)) {
                                tellHowVO.setPoseId(poseId);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取泰豪结果出错", e);
        }
        return tellHowVO;
    }
}
