package com.example.database.fanyumeta.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.database.fanyumeta.client.vo.CurrentGridFailure;
import com.example.database.fanyumeta.client.vo.GridRisk;
import com.example.database.fanyumeta.client.vo.GroundWire;
import com.example.database.fanyumeta.client.vo.LineLoadRate;
import com.example.database.fanyumeta.client.vo.NumMinusOneDetails;
import com.example.database.fanyumeta.client.vo.OverhaulWorkList;
import com.example.database.fanyumeta.client.vo.PowerSupplyInfo;
import com.example.database.fanyumeta.client.vo.TellHowCurrentGridFailureVO;
import com.example.database.fanyumeta.client.vo.TellHowCurveVO;
import com.example.database.fanyumeta.client.vo.TellHowGridRiskVO;
import com.example.database.fanyumeta.client.vo.TellHowGroundWireVO;
import com.example.database.fanyumeta.client.vo.TellHowImportantUserVO;
import com.example.database.fanyumeta.client.vo.TellHowLineLoadRateVO;
import com.example.database.fanyumeta.client.vo.TellHowNumMinusOneDetailsVO;
import com.example.database.fanyumeta.client.vo.TellHowOverhaulWorkListVO;
import com.example.database.fanyumeta.client.vo.TellHowPowerSupplyInfoVO;
import com.example.database.fanyumeta.client.vo.TellHowTransLoadRateVO;
import com.example.database.fanyumeta.client.vo.TellHowVO;
import com.example.database.fanyumeta.client.vo.TransLoadRate;
import com.example.database.fanyumeta.utils.StringUtils;
import com.example.database.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                5：与电网一张图联动，泰豪前端联动，梵钰播报，挥左边
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
        String url = this.tellHowProperties.getServiceAddr() + "/data/dutyPersonnelInfo";
        Map<String, String> params = new HashMap<>();
        params.put("dateTime", dutyDateStr);
        if (StringUtils.hasText(dutyOrder)) {
            params.put("dutyOrderName", dutyOrder);
        }
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
                        dutyInfo.append(noticeDate);
                        if (StringUtils.hasText(dutyOrder)) {
                            dutyInfo.append(dutyOrder);
                        }
                        dutyInfo.append("值班人员为：");
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

    /**
     * 获取雄安总加负荷曲线
     *
     * @param date 日期
     * @return 曲线数据
     */
    public TellHowCurveVO totalLoadCurve(LocalDate date) {
        String dateTime = StringUtils.getDateStr(date) + " 00:00:00";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("dateTime", dateTime);
        params.put("actionType", actionType);
        String url = this.tellHowProperties.getServiceAddr() + "/data/totalLoadCurve";
        JSONObject res = sendMessage(url, params);
        TellHowCurveVO tellHowCurveVO = this.parserLoadCurveData(res);
        return tellHowCurveVO;
    }

    /**
     * 获取雄安分片区负荷曲线
     *
     * @param date 日期
     * @return 曲线数据
     */
    public TellHowCurveVO zoneLoadCurve(LocalDate date, Area area) {
        String dateTime = StringUtils.getDateStr(date) + " 00:00:00";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("dateTime", dateTime);
        params.put("actionType", actionType);
        params.put("area", area.value);
        String url = this.tellHowProperties.getServiceAddr() + "/data/zoneLoadCurve";
        JSONObject res = sendMessage(url, params);
        TellHowCurveVO tellHowCurveVO = this.parserLoadCurveData(res);
        return tellHowCurveVO;
    }

    /**
     * 获取主变负载率
     *
     * @return 主变负载率
     */
    public TellHowTransLoadRateVO transLoadRate() {
        TellHowTransLoadRateVO tellHowTransLoadRateVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/transLoadRate";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        JSONObject res = sendMessage(url, params);
        tellHowTransLoadRateVO = this.parserTransLoadRateData(res);
        return tellHowTransLoadRateVO;
    }

    /**
     * 获取线路负载率
     *
     * @return 线路负载率
     */
    public TellHowLineLoadRateVO lineLoadRate() {
        TellHowLineLoadRateVO tellHowLineLoadRateVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/lineLoadRate";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        JSONObject res = sendMessage(url, params);
        tellHowLineLoadRateVO = this.parserLineLoadRateData(res);
        return tellHowLineLoadRateVO;
    }

    /**
     * 获取 N-1 详情
     *
     * @return N-1 详情
     */
    public TellHowNumMinusOneDetailsVO numMinusOneDetails() {
        TellHowNumMinusOneDetailsVO tellHowNumMinusOneDetailsVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/num/numMinusOneDetails";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        JSONObject res = sendMessage(url, params);
        tellHowNumMinusOneDetailsVO = this.parserNumMinusOneDetailsData(res);
        return tellHowNumMinusOneDetailsVO;
    }

    /**
     * 获取重要用户
     *
     * @return 重要用户
     */
    public TellHowImportantUserVO importantUser() {
        TellHowImportantUserVO tellHowImportantUserVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/importantUser";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        params.put("year", String.valueOf(LocalDate.now().getYear()));
        JSONObject res = sendMessage(url, params);
        tellHowImportantUserVO = this.parserImportantUserData(res);
        return tellHowImportantUserVO;
    }

    /**
     * 获取当前电网故障
     *
     * @return 当前电网故障
     */
    public TellHowCurrentGridFailureVO currentGridFailure() {
        TellHowCurrentGridFailureVO tellHowCurrentGridFailureVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/currentGridFailure";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        JSONObject res = sendMessage(url, params);
        tellHowCurrentGridFailureVO = this.parserCurrentGridFailureData(res);
        return tellHowCurrentGridFailureVO;
    }

    /**
     * 获取保电信息
     *
     * @param date 保电开始日期
     * @return 保电信息
     */
    public TellHowPowerSupplyInfoVO powerSupplyInfo(String date) {
        TellHowPowerSupplyInfoVO tellHowPowerSupplyInfoVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/powerSupplyInfo";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        params.put("dateTime", date + " 00:00:00");
        JSONObject res = sendMessage(url, params);
        tellHowPowerSupplyInfoVO = this.parserPowerSupplyInfoData(res);
        return tellHowPowerSupplyInfoVO;
    }

    /**
     * 获取检修工作列表
     *
     * @param date 日期
     * @param status 检修状态
     * @return 检修工作列表
     */
    public TellHowOverhaulWorkListVO overhaulWorkList(LocalDate date, OverhaulWorkListStatus status) {
        TellHowOverhaulWorkListVO tellHowOverhaulWorkListVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/overhaulWorkList";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        if (null != date) {
            params.put("dateTime", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00");
        }
        params.put("status", status.getValue());
        JSONObject res = sendMessage(url, params);
        tellHowOverhaulWorkListVO = this.parserOverhaulWorkListData(res);
        return tellHowOverhaulWorkListVO;
    }

    /**
     * 获取电网风险
     *
     * @return 电网风险
     */
    public TellHowGridRiskVO gridRisk() {
        TellHowGridRiskVO tellHowGridRiskVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/gridRisk";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        JSONObject res = sendMessage(url, params);
        tellHowGridRiskVO = this.parserGridRiskData(res);
        return tellHowGridRiskVO;
    }

    /**
     * 获取地线数据
     *
     * @return 地线数据
     */
    public TellHowGroundWireVO groundWire() {
        TellHowGroundWireVO tellHowGroundWireVO = null;
        String url = this.tellHowProperties.getServiceAddr() + "/data/groundWire";
        String actionType = ActionType.THREE.value;
        Map<String, String> params = new HashMap<>();
        params.put("actionType", actionType);
        JSONObject res = sendMessage(url, params);
        tellHowGroundWireVO = this.parserGroundWireData(res);
        return tellHowGroundWireVO;
    }

    /**
     * 发送请求
     *
     * @param url 请求地址
     * @param params 请求参数
     * @return 响应结果
     */
    private JSONObject sendMessage(String url, Map<String, String> params) {
        JSONObject data = null;
        try {
            String responseData = HttpClientUtil.sendGet(url , params);
            log.info("调用泰豪接口：{}，请求参数：{}，响应结果：{}", url, params, responseData);
            JSONObject res = JSONObject.parseObject(responseData);
            if (res.getIntValue("code") == 0) {
                data = res.getJSONObject("data");
            } else {
                log.error("泰豪响应结果不正确");
            }
        } catch (Exception e) {
            log.error("调用泰豪接口出错", e);
        }
        return data;
    }

    /**
     * 解析负荷曲线曲线数据
     *
     * @param data 泰豪接口返回数据
     * @return 负荷曲线曲线数据
     */
    private TellHowCurveVO parserLoadCurveData(JSONObject data) {
        TellHowCurveVO tellHowCurveVO = null;
        if (null != data) {
            JSONObject resData = data.getJSONObject("resData");
            if (null != resData) {
                tellHowCurveVO = new TellHowCurveVO();
                JSONObject todayCurve = resData.getJSONObject("todayCurve");
                if (null != todayCurve) {
                    String maxValue = todayCurve.getString("max");
                    if (StringUtils.hasText(maxValue)) {
                        tellHowCurveVO.setDateMaxValue(maxValue);
                    }
                }
            }
            JSONObject actionData = data.getJSONObject("actionData");
            if (null != actionData) {
                String poseId = actionData.getString("poseId");
                if (StringUtils.hasText(poseId)) {
                    tellHowCurveVO.setPoseId(poseId);
                }
            }
        }
        return tellHowCurveVO;
    }


    /**
     * 解析主变负荷率
     *
     * @param data 泰豪接口返回数据
     * @return 主变负荷率
     */
    private TellHowTransLoadRateVO parserTransLoadRateData(JSONObject data) {
        TellHowTransLoadRateVO tellHowTransLoadRateVO = null;
        if (null != data) {
            tellHowTransLoadRateVO = new TellHowTransLoadRateVO();
            JSONArray resData = data.getJSONArray("resData");
            if (null != resData) {
                List<TransLoadRate> transLoadRateList = new ArrayList<>();
                for (int i = 0; i < resData.size(); i++) {
                    JSONObject transLoadRateItem = resData.getJSONObject(i);
                    if (null != transLoadRateItem) {
                        TransLoadRate transLoadRate = new TransLoadRate();
                        transLoadRate.setDevName(transLoadRateItem.getString("devName"));
                        transLoadRate.setStName(transLoadRateItem.getString("stName"));
                        transLoadRate.setMaxRate(transLoadRateItem.getString("maxRate"));
                        transLoadRate.setRealtimeRate(transLoadRateItem.getString("realtimeRate"));
                        transLoadRate.setFullName(transLoadRate.getStName() + StringUtils.replaceSpecialSymbol(transLoadRate.getDevName()));
                        transLoadRateList.add(transLoadRate);
                    }
                }
                tellHowTransLoadRateVO.setTransLoadRateList(transLoadRateList);
            }
            JSONObject actionData = data.getJSONObject("actionData");
            if (null != actionData) {
                String poseId = actionData.getString("poseId");
                tellHowTransLoadRateVO.setPoseId(poseId);
            }
        }
        return tellHowTransLoadRateVO;
    }

    /**
     * 解析线路负载率
     *
     * @param res 泰豪接口返回数据
     * @return 线路负载率
     */
    private TellHowLineLoadRateVO parserLineLoadRateData(JSONObject res) {
        TellHowLineLoadRateVO tellHowLineLoadRateVO = null;
        if (null != res) {
            tellHowLineLoadRateVO = new TellHowLineLoadRateVO();
            JSONArray resData = res.getJSONArray("resData");
            if (null != resData) {
                List<LineLoadRate> lineLoadRateList = new ArrayList<>();
                for (int i = 0; i < resData.size(); i++) {
                    JSONObject lineLoadRateItem = resData.getJSONObject(i);
                    if (null != lineLoadRateItem) {
                        LineLoadRate lineLoadRate = new LineLoadRate();
                        lineLoadRate.setLineName(lineLoadRateItem.getString("devName"));
                        lineLoadRate.setMaxRate(lineLoadRateItem.getString("maxRate"));
                        lineLoadRate.setRealtimeRate(lineLoadRateItem.getString("realtimeRate"));;
                        lineLoadRateList.add(lineLoadRate);
                    }
                }
                tellHowLineLoadRateVO.setLineLoadRateList(lineLoadRateList);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (null != actionData) {
                String poseId = actionData.getString("poseId");
                tellHowLineLoadRateVO.setPoseId(poseId);
            }
        }
        return tellHowLineLoadRateVO;
    }

    /**
     * 解析 N-1 详情
     *
     * @param res 泰豪接口返回数据
     * @return N-1 详情
     */
    private TellHowNumMinusOneDetailsVO parserNumMinusOneDetailsData(JSONObject res) {
        TellHowNumMinusOneDetailsVO tellHowNumMinusOneDetailsVO = null;
        if (null != res) {
            tellHowNumMinusOneDetailsVO = new TellHowNumMinusOneDetailsVO();
            JSONArray resData = res.getJSONArray("resData");
            if (null != resData) {
                List<NumMinusOneDetails> numMinusOneDetailsList = new ArrayList<>();
                for (int i = 0; i < resData.size(); i++) {
                    JSONObject numMinusOneDetailsItem = resData.getJSONObject(i);
                    if (null != numMinusOneDetailsItem) {
                        JSONArray childrenItem = numMinusOneDetailsItem.getJSONArray("childrenItem");
                        if (null != childrenItem) {
                            for (int j = 0; j < childrenItem.size(); j++) {
                                JSONObject childrenItemItem = childrenItem.getJSONObject(j);
                                if (null != childrenItemItem) {
                                    NumMinusOneDetails numMinusOneDetails = new NumMinusOneDetails();
                                    numMinusOneDetails.setNumber(childrenItemItem.getString("number"));
                                    numMinusOneDetails.setDevName(childrenItemItem.getString("devName"));
                                    numMinusOneDetails.setStName(childrenItemItem.getString("stName"));
                                    numMinusOneDetailsList.add(numMinusOneDetails);
                                }
                            }
                        }
                    }
                }
                tellHowNumMinusOneDetailsVO.setNumMinusOneDetailsList(numMinusOneDetailsList);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (null != actionData) {
                String poseId = actionData.getString("poseId");
                tellHowNumMinusOneDetailsVO.setPoseId(poseId);
            }
        }
        return tellHowNumMinusOneDetailsVO;
    }

    /**
     * 解析重要用户数据
     *
     * @param res 泰豪接口返回数据
     * @return 泰豪重要客户信息
     */
    private TellHowImportantUserVO parserImportantUserData(JSONObject res) {
        TellHowImportantUserVO tellHowImportantUserVO = null;
        if (res != null) {
            JSONObject resData = res.getJSONObject("resData");
            if (resData != null) {
                tellHowImportantUserVO = new TellHowImportantUserVO();
                String total = resData.getString("total");
                tellHowImportantUserVO.setTotal(total);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (actionData != null) {
                String poseId = actionData.getString("poseId");
                if (StringUtils.hasText(poseId)) {
                    tellHowImportantUserVO.setPoseId(poseId);
                }
            }
        }
        return tellHowImportantUserVO;
    }

    /**
     * 解析当前电网故障
     *
     * @param res 泰豪接口返回数据
     * @return 当前电网故障
     */
    private TellHowCurrentGridFailureVO parserCurrentGridFailureData(JSONObject res) {
        TellHowCurrentGridFailureVO tellHowCurrentGridFailureVO = null;
        if (res != null) {
            tellHowCurrentGridFailureVO = new TellHowCurrentGridFailureVO();
            JSONArray resData = res.getJSONArray("resData");
            if (resData != null) {
                List<CurrentGridFailure> currentGridFailureList = new ArrayList<>();
                for (int i = 0; i < resData.size(); i++) {
                    JSONObject jsonObject = resData.getJSONObject(i);
                    if (jsonObject != null) {
                        CurrentGridFailure currentGridFailure = new CurrentGridFailure();
                        currentGridFailure.setStatus(jsonObject.getString("status"));
                        currentGridFailure.setFaultStartTime(jsonObject.getString("faultStartTime"));
                        currentGridFailure.setFaultDescribe(jsonObject.getString("faultDescribe"));
                        currentGridFailure.setFaultType(jsonObject.getString("faultType"));
                        currentGridFailureList.add(currentGridFailure);
                    }
                }
                tellHowCurrentGridFailureVO.setCurrentGridFailureList(currentGridFailureList);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (actionData != null) {
                tellHowCurrentGridFailureVO.setPoseId(actionData.getString("poseId"));
            }
        }
        return tellHowCurrentGridFailureVO;
    }

    /**
     * 解析保电信息
     *
     * @param res 泰豪接口返回数据
     * @return 保电信息
     */
    private TellHowPowerSupplyInfoVO parserPowerSupplyInfoData(JSONObject res) {
        TellHowPowerSupplyInfoVO tellHowPowerSupplyInfoVO = null;
        if (res != null) {
            JSONArray resData = res.getJSONArray("resData");
            if (resData != null && resData.size() > 0) {
                tellHowPowerSupplyInfoVO = new TellHowPowerSupplyInfoVO();
                List<PowerSupplyInfo> powerSupplyInfoList = new ArrayList<>();
                for (int i = 0; i < resData.size(); i++) {
                    JSONObject jsonObject = resData.getJSONObject(i);
                    PowerSupplyInfo powerSupplyInfo = new PowerSupplyInfo();
                    powerSupplyInfo.setStartTime(jsonObject.getString("startTime"));
                    powerSupplyInfo.setTaskName(jsonObject.getString("taskName"));
                    JSONArray userList = jsonObject.getJSONArray("userList");
                    if (userList != null && userList.size() > 0) {
                        List<String> userListList = new ArrayList<>();
                        for (int j = 0; j < userList.size(); j++) {
                            userListList.add(userList.getString(j));
                        }
                        powerSupplyInfo.setUserList(String.join(",", userListList));
                    }
                    powerSupplyInfoList.add(powerSupplyInfo);
                }
                tellHowPowerSupplyInfoVO.setPowerSupplyInfoList(powerSupplyInfoList);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (actionData != null) {
                tellHowPowerSupplyInfoVO.setPoseId(actionData.getString("poseId"));
            }
        }
        return tellHowPowerSupplyInfoVO;
    }

    /**
     * 解析检修工作列表
     *
     * @param res 泰豪接口返回数据
     * @return 检修工作列表
     */
    private TellHowOverhaulWorkListVO parserOverhaulWorkListData(JSONObject res) {
        TellHowOverhaulWorkListVO tellHowOverhaulWorkListVO = null;
        if (res != null) {
            JSONObject resData = res.getJSONObject("resData");
            if (resData != null) {
                tellHowOverhaulWorkListVO = new TellHowOverhaulWorkListVO();
                List<OverhaulWorkList> overhaulWorkListList = new ArrayList<>();
                JSONArray executingList = resData.getJSONArray("executingList");
                this.parserOverhaulWorkList(overhaulWorkListList, executingList);
                JSONArray toBeExecuteList = resData.getJSONArray("toBeExecuteList");
                this.parserOverhaulWorkList(overhaulWorkListList, toBeExecuteList);
                tellHowOverhaulWorkListVO.setOverhaulWorkListList(overhaulWorkListList);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (actionData != null) {
                tellHowOverhaulWorkListVO.setPoseId(actionData.getString("poseId"));
            }
        }
        return tellHowOverhaulWorkListVO;
    }

    /**
     * 解析检修工作列表
     *
     * @param overhaulWorkListList 解析后检修工作列表
     * @param items 待解析的检修工作列表
     */
    private void parserOverhaulWorkList(List<OverhaulWorkList> overhaulWorkListList, JSONArray items) {
        if (items != null && items.size() > 0) {
            for (int i = 0; i < items.size(); i++) {
                JSONObject jsonObject = items.getJSONObject(i);
                OverhaulWorkList overhaulWorkList = new OverhaulWorkList();
                overhaulWorkList.setWorkContent(jsonObject.getString("workContent"));
                overhaulWorkList.setPowercutType(jsonObject.getString("powercutType"));
                overhaulWorkList.setStatus(jsonObject.getString("status"));
                overhaulWorkList.setStartTime(jsonObject.getString("startTime"));
                overhaulWorkList.setEndTime(jsonObject.getString("endTime"));
                overhaulWorkListList.add(overhaulWorkList);
            }
        }
    }

    /**
     * 解析电网风险数据
     *
     * @param res 泰豪接口返回数据
     * @return 电网风险数据
     */
    private TellHowGridRiskVO parserGridRiskData(JSONObject res) {
        TellHowGridRiskVO tellHowGridRiskVO = null;
        if (null != res) {
            tellHowGridRiskVO = new TellHowGridRiskVO();
            JSONArray resData = res.getJSONArray("resData");
            if (null != resData && resData.size() > 0) {
                List<GridRisk> gridRiskList = new ArrayList<>();
                for (int i = 0; i < resData.size(); i++) {
                    JSONObject jsonObject = resData.getJSONObject(i);
                    GridRisk gridRisk = new GridRisk();
                    gridRisk.setNumber(jsonObject.getString("number"));
                    gridRisk.setStationName(jsonObject.getString("stationName"));
                    gridRisk.setEventLevel(jsonObject.getString("eventLevel"));
                    gridRiskList.add(gridRisk);
                }
                tellHowGridRiskVO.setGridRiskList(gridRiskList);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (null != actionData) {
                tellHowGridRiskVO.setPoseId(actionData.getString("poseId"));
            }
        }
        return tellHowGridRiskVO;
    }

    /**
     * 解析地线数据
     *
     * @param res 泰豪接口返回数据
     * @return 地线数据
     */
    private TellHowGroundWireVO parserGroundWireData(JSONObject res) {
        TellHowGroundWireVO tellHowGroundWireVO = null;
        if (null != res) {
            tellHowGroundWireVO = new TellHowGroundWireVO();
            JSONArray resData = res.getJSONArray("resData");
            if (null != resData) {
                List<GroundWire> groundWireList = new ArrayList<>();
                for (int i = 0; i < resData.size(); i++) {
                    JSONObject jsonObject = resData.getJSONObject(i);
                    GroundWire groundWire = new GroundWire();
                    groundWire.setNumber(jsonObject.getString("number"));
                    groundWire.setGroundWireName(jsonObject.getString("groundWireName"));
                    groundWire.setGroundWireCreateTime(jsonObject.getString("groundWireCreateTime"));
                    groundWire.setDevicePosition(jsonObject.getString("devicePosition"));
                    groundWire.setGroundWireSuspensionCreateUser(jsonObject.getString("groundWireSuspensionCreateUser"));
                    groundWire.setGroundWireSuspensionCreateTime(jsonObject.getString("groundWireSuspensionCreateTime"));
                    groundWireList.add(groundWire);
                }
                tellHowGroundWireVO.setGroundWireList(groundWireList);
            }
            JSONObject actionData = res.getJSONObject("actionData");
            if (null != actionData) {
                tellHowGroundWireVO.setPoseId(actionData.getString("poseId"));
            }
        }
        return tellHowGroundWireVO;
    }

    /**
     * 动作类型
     */
    private enum ActionType {
        /*
        1. actionType（必填）: 动作类型
                1：文字的结果，框里面显示，梵钰播报，动作挥中间
                2：数据需要组织用列表或者折线图表，梵钰组织界面梵钰播报，调开图链接，挥右边
                3：数据本身大屏有的，泰豪前端做下高亮显示，梵钰播报。按实际方位挥
                4：文档和大百科，梵钰组织界面调开图链接，梵钰播报，挥右边
                5：与电网一张图联动，泰豪前端联动，梵钰播报，挥左边
                6：开接线图时挥左边在电网接线图与数智人中间
         */

        /**
         * 1：文字的结果，框里面显示，梵钰播报，动作挥中间
         */
        ONE("1"),

        /**
         * 2：数据需要组织用列表或者折线图表，梵钰组织界面梵钰播报，调开图链接，挥右边
         */
        TWO("2"),

        /**
         * 3：数据本身大屏有的，泰豪前端做下高亮显示，梵钰播报。按实际方位挥
         */
        THREE("3"),

        /**
         * 4：文档和大百科，梵钰组织界面调开图链接，梵钰播报，挥右边
         */
        FOUR("4"),

        /**
         * 5：与电网一张图联动，泰豪前端联动，梵钰播报，挥左边
         */
        FIVE("5"),

        /**
         * 6：开接线图时挥左边在电网接线图与数智人中间
         */
        SIX("6");

        private String value;

        ActionType(String value) {
            this.value = value;
        }
    }

    public enum Area {

        // 安新县：1
        AN_XIN(1),
        // 容城县：2
        RONG_CHENG(2),
        // 雄县县城：3
        XIONG_XIAN(3),
        // 沧州片区：4
        CANG_ZHOU(4),
        // 容东片区：5
        RONG_DONG(5),
        // 容西片区：6
        RONG_XI(6),
        // 雄东片区：7
        XIONG_DONG(7),
        // 启动区：8
        QI_DONG(8),
        // 目标电网：9
        MU_BIAO(9),
        // 雄县(含沧州)：10
        XIONG_XIAN_CANG_ZHOU(10);


        Area(Integer value) {
            this.value = value == null ? "" : value.toString();
        }

        private String value;

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 检修工单状态
     */
    public enum OverhaulWorkListStatus {

        /**
         * 待开工
         */
        WAIT_FOR_START("1"),

        /**
         * 执行中
         */
        EXECUTING("2");

        OverhaulWorkListStatus(String value) {
            this.value = value;
        }

        private String value;

        public String getValue() {
            return this.value;
        }
    }
}
