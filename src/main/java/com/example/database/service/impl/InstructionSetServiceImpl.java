package com.example.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.contant.MyContants;
import com.example.database.domain.AnnualElectricityConsumption;
import com.example.database.domain.Generatrix;
import com.example.database.domain.InstructionSet;
import com.example.database.domain.Line;
import com.example.database.domain.RiskAnalysis;
import com.example.database.entity.Generatrix1;
import com.example.database.entity.Generatrix3;
import com.example.database.entity.InterlocutionResult;
import com.example.database.entity.ReturnVo;
import com.example.database.fanyumeta.client.HardwareControlClient;
import com.example.database.fanyumeta.client.NanRuiClient;
import com.example.database.fanyumeta.client.TellHowClient;
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
import com.example.database.fanyumeta.client.vo.TellHowLoadMovementMainNetVO;
import com.example.database.fanyumeta.client.vo.TellHowLoadMovementNewEnergyVO;
import com.example.database.fanyumeta.client.vo.TellHowNumMinusOneDetailsVO;
import com.example.database.fanyumeta.client.vo.TellHowOverhaulWorkListVO;
import com.example.database.fanyumeta.client.vo.TellHowPowerSupplyInfoVO;
import com.example.database.fanyumeta.client.vo.TellHowTransLoadRateVO;
import com.example.database.fanyumeta.client.vo.TellHowVO;
import com.example.database.fanyumeta.client.vo.TransLoadRate;
import com.example.database.fanyumeta.entity.MaxLoad;
import com.example.database.fanyumeta.server.ServiceType;
import com.example.database.fanyumeta.server.TellHowServer;
import com.example.database.fanyumeta.server.tellhow.ResponseMessage;
import com.example.database.fanyumeta.service.LoadService;
import com.example.database.fanyumeta.utils.SimilarityUtil;
import com.example.database.mapper.InstructionSetMapper;
import com.example.database.service.AnnualElectricityConsumptionService;
import com.example.database.service.GeneratrixService;
import com.example.database.service.InstructionSetService;
import com.example.database.service.LineService;
import com.example.database.service.RiskAnalysisService;
import com.example.database.service.TransformerService;
import com.example.database.utils.ArabicNumeralsUtil;
import com.example.database.utils.GeneratrixUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author woshi
 * @description 针对表【instruction_set】的数据库操作Service实现
 * @createDate 2023-09-20 09:56:26
 */
@Service
@Slf4j
public class InstructionSetServiceImpl extends ServiceImpl<InstructionSetMapper, InstructionSet>
        implements InstructionSetService {
    @Autowired
    private Environment env;
    @Autowired
    private AnnualElectricityConsumptionService annualElectricityConsumptionService;
    @Autowired
    private LineService lineService;
    @Autowired
    private GeneratrixService generatrixService;
    @Autowired
    private RiskAnalysisService riskAnalysisService;
    @Autowired
    private TransformerService transformerService;

    /**
     * 相似度阈值
     */
    private static final double SIMILARITY_THRESHOLD = 0.8;

    /**
     * 中控系统客户端
     */
    @Resource
    private HardwareControlClient hardwareControlClient;

    /**
     * 泰豪服务客户端
     */
    @Resource
    private TellHowClient tellHowClient;

    /**
     * 南瑞客户端
     */
    @Resource
    private NanRuiClient nanRuiClient;


    @Resource
    private LoadService loadService;

    @Value("${fan-yu.error-message}")
    private String errorMessage;

    public String getEnv() {
        return this.env.getProperty("interlocutionUrl");
    }

    public String getLargeModelUrl() {
        return this.env.getProperty("fan-yu.large-model.url");
    }

    @Override
    public void haveReturnVo(InterlocutionResult ilResult, ReturnVo returnVo, String message) {
        int serialNum = Integer.valueOf(ilResult.getId());
        String directive = ilResult.getDirective();
        String directiveType = ilResult.getDirectiveType();
        returnVo.setId(ilResult.getId());
        //运行指令
        if (MyContants.YX_ZL.equals(directiveType)) {
            returnVo.setResults(MyContants.YX_ZL_ANS);

            String hardwareCommandResult = hardwareControlClient.sendMessage(message);
            if (StringUtils.isNotBlank(hardwareCommandResult)) {
                returnVo.setResults(hardwareCommandResult);
            } else {
                returnVo.setResults(errorMessage);
            }
        }
        //处理图片信息
        this.haveJpgPath(ilResult, returnVo);
        //数据库指令
        if (MyContants.SJK_ZL.equals(directiveType)) {
            if (serialNum == 35 || serialNum == 103) {  // 雄安总负荷/分片区负荷
                if (regexIsFind("负荷", message)) {
                    if (regexIsFind("今日|今天", message)) {
                        TellHowClient.Area area = this.getArea(message);
                        ResponseMessage.TellHowMenu menu = null;
                        if (TellHowClient.Area.AN_XIN == area) {
                            menu = ResponseMessage.TellHowMenu.AN_XIN_LOAD_CURVE;
                        } else if (TellHowClient.Area.RONG_CHENG == area) {
                            menu = ResponseMessage.TellHowMenu.RONG_CHENG_LOAD_CURVE;
                        } else if (TellHowClient.Area.XIONG_XIAN == area) {
                            menu = ResponseMessage.TellHowMenu.XIONG_XIAN_LOAD_CURVE;
                        } else if (TellHowClient.Area.CANG_ZHOU == area) {
                            menu = ResponseMessage.TellHowMenu.CANG_ZHOU_LOAD_CURVE;
                        } else if (TellHowClient.Area.RONG_DONG == area) {
                            menu = ResponseMessage.TellHowMenu.RONG_DONG_LOAD_CURVE;
                        } else if (TellHowClient.Area.RONG_XI == area) {
                            menu = ResponseMessage.TellHowMenu.RONG_XI_LOAD_CURVE;
                        } else if (TellHowClient.Area.XIONG_DONG == area) {
                            menu = ResponseMessage.TellHowMenu.XIONG_DONG_LOAD_CURVE;
                        } else if (TellHowClient.Area.QI_DONG == area) {
                            menu = ResponseMessage.TellHowMenu.QI_DONG_LOAD_CURVE;
                        } else if (TellHowClient.Area.MU_BIAO == area) {
                            menu = ResponseMessage.TellHowMenu.MU_BIAO_LOAD_CURVE;
                        } else if (TellHowClient.Area.XIONG_XIAN_CANG_ZHOU == area) {
                            menu = ResponseMessage.TellHowMenu.XIONG_XIAN_CANG_ZHOU_LOAD_CURVE;
                        }
                        TellHowCurveVO tellHowCurveVO = null;
                        if (null != area) {
                            tellHowCurveVO = this.tellHowClient.zoneLoadCurve(LocalDate.now(), area);
                            this.setCompareValue(tellHowCurveVO, area);
                        } else {
                            tellHowCurveVO = this.tellHowClient.totalLoadCurve(LocalDate.now());
                            this.setCompareValue(tellHowCurveVO, null);
                            menu = ResponseMessage.TellHowMenu.TOTAL_LOAD_CURVE;
                        }
                        if (null != tellHowCurveVO) {
                            String maxValue = tellHowCurveVO.getDateMaxValue();
                            if (StringUtils.isNotBlank(maxValue)) {
                                String resultContent = message.replace("多少", maxValue + "MW");
                                String compareYesterday = tellHowCurveVO.getCompareYesterday();
                                if (StringUtils.isNotBlank(compareYesterday)) {
                                    resultContent += "，较昨日增长" + compareYesterday + "%";
                                }
                                String compareLastYear = tellHowCurveVO.getCompareLastYear();
                                if (StringUtils.isNotBlank(compareLastYear)) {
                                    resultContent += "，较去年增长" + compareLastYear + "%";
                                }
                                returnVo.setResults(resultContent);
                                String poseId = tellHowCurveVO.getPoseId();
                                if (StringUtils.isNotBlank(poseId)) {
                                    returnVo.setPoseId(poseId);
                                }
                                noticeTellHowAction(menu);
                            }
                        }
                    } else if (regexIsFind("历史", message)) {
                        TellHowClient.Area area = this.getArea(message);
                        String maxValue = this.loadService.getHistoryMaxValue(null == area ? null : area.getValue());
                        String resultContent = message.replace("多少", maxValue + "MW");
                        returnVo.setResults(resultContent);
                    } else if (regexIsFind("今年", message)) {
                        TellHowClient.Area area = this.getArea(message);
                        String maxValue = this.loadService.getYearMaxValue(null == area ? null : area.getValue());
                        String resultContent = message.replace("多少", maxValue + "MW");
                        returnVo.setResults(resultContent);
                    }
                }
            } else if (serialNum == 307) {  // 主变负载率
                String text = message.replaceAll("(负载率|是多少)", "");
                TellHowTransLoadRateVO tellHowTransLoadRateVO = this.tellHowClient.transLoadRate();
                String result = null;
                if (null != tellHowTransLoadRateVO) {
                    List<TransLoadRate> transLoadRateList = tellHowTransLoadRateVO.getTransLoadRateList();
                    if (ObjectUtils.isNotEmpty(transLoadRateList)) {
                        for (TransLoadRate transLoadRate : transLoadRateList) {
                            String fullName = transLoadRate.getFullName();
                            if (StringUtils.isNotBlank(fullName)) {
                                double similarity = SimilarityUtil.getSimilarity(fullName, text);
                                log.info("【相似度】【{}】与【{}】的为：{}", fullName, text, similarity);
                                if (similarity > SIMILARITY_THRESHOLD) {
                                    StringBuilder rate = new StringBuilder();
                                    String maxRate = transLoadRate.getMaxRate();
                                    rate.append(transLoadRate.getRealtimeRate());
                                    if (StringUtils.isNotBlank(maxRate)) {
                                        rate.append(", 最大负载率是").append(maxRate);
                                    }
                                    result = message.replaceAll("多少", rate.toString());
                                    String poseId = tellHowTransLoadRateVO.getPoseId();
                                    if (StringUtils.isNotBlank(poseId)) {
                                        returnVo.setPoseId(poseId);
                                    }
                                    noticeTellHowAction(ResponseMessage.TellHowMenu.TRANS_LOAD_RATE);
                                    break;
                                }
                            }
                        }
                    }
                }
//                if (StringUtils.isNotBlank(result)) {
//                    result = "未查询到相关信息";
//                }
                returnVo.setResults(result);
            } else if (serialNum == 308) {  // 线路负载率
                String text = message.replaceAll("(负载率|是多少)", "");
                TellHowLineLoadRateVO tellHowLineLoadRateVO = this.tellHowClient.lineLoadRate();
                if (null != tellHowLineLoadRateVO) {
                    List<LineLoadRate> lineLoadRateList = tellHowLineLoadRateVO.getLineLoadRateList();
                    if (ObjectUtils.isNotEmpty(lineLoadRateList)) {
                        for (LineLoadRate lineLoadRate : lineLoadRateList) {
                            String lineName = lineLoadRate.getLineName();
                            if (StringUtils.isNotBlank(lineName)) {
                                double similarity = SimilarityUtil.getSimilarity(lineName, text);
                                log.info("【相似度】【{}】与【{}】的为：{}", lineName, text, similarity);
                                if (similarity > SIMILARITY_THRESHOLD) {
                                    StringBuilder rate = new StringBuilder();
                                    rate.append(lineLoadRate.getRealtimeRate());
                                    String lineMaxRate = lineLoadRate.getMaxRate();
                                    if (StringUtils.isNotBlank(lineMaxRate)) {
                                        rate.append(", 最大负载率是").append(lineMaxRate);
                                    }
                                    String result = message.replaceAll("多少", rate.toString());
                                    returnVo.setResults(result);
                                    String poseId = tellHowLineLoadRateVO.getPoseId();
                                    if (StringUtils.isNotBlank(poseId)) {
                                        returnVo.setPoseId(poseId);
                                    }
                                    noticeTellHowAction(ResponseMessage.TellHowMenu.LINE_LOAD_RATE);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (serialNum == 309) {  // n-1明细
                String notice = null;
                TellHowNumMinusOneDetailsVO tellHowNumMinusOneDetailsVO = this.tellHowClient.numMinusOneDetails();
                if (null != tellHowNumMinusOneDetailsVO) {
                    List<NumMinusOneDetails> numMinusOneDetailsList = tellHowNumMinusOneDetailsVO.getNumMinusOneDetailsList();
                    if (ObjectUtils.isNotEmpty(numMinusOneDetailsList)) {
                        List<String> deviceNameList = new ArrayList<>();
                        for (NumMinusOneDetails numMinusOneDetails : numMinusOneDetailsList) {
                            String devName = numMinusOneDetails.getDevName();
                            if (StringUtils.isNotBlank(devName)) {
                                deviceNameList.add(devName);
                            }
                        }
                        if (ObjectUtils.isNotEmpty(deviceNameList)) {
                           notice = "当前" + StringUtils.join(deviceNameList, ",") + "设备存在 N-1 风险";
                           returnVo.setPoseId(tellHowNumMinusOneDetailsVO.getPoseId());
                           noticeTellHowAction(ResponseMessage.TellHowMenu.NUM_MINUS_ONE);
                        }
                    }
                }
                if (StringUtils.isBlank(notice)) {
                    notice = "当前没有 N-1 风险";
                }
                returnVo.setResults(notice);
            } else if (serialNum == 310) {  // 重要用户统计
                String result = null;
                TellHowImportantUserVO tellHowImportantUserVO = this.tellHowClient.importantUser();
                if (null != tellHowImportantUserVO) {
                    String total = tellHowImportantUserVO.getTotal();
                    if (StringUtils.isNotBlank(total)) {
                        result = "重要用户总数是" + total;
                        returnVo.setPoseId(tellHowImportantUserVO.getPoseId());
                        noticeTellHowAction(ResponseMessage.TellHowMenu.IMPORTANT_USER);
                    }
                }
                returnVo.setResults(result);
            } else if (serialNum == 311) {  // 当前电网故障事件
                String result = null;
                TellHowCurrentGridFailureVO tellHowCurrentGridFailureVO = this.tellHowClient.currentGridFailure();
                if (null != tellHowCurrentGridFailureVO) {
                    List<CurrentGridFailure> currentGridFailureList = tellHowCurrentGridFailureVO
                            .getCurrentGridFailureList();
                    if (ObjectUtils.isNotEmpty(currentGridFailureList)) {
                        List<String> faultDescribeList = new ArrayList<>();
                        for (CurrentGridFailure currentGridFailure : currentGridFailureList) {
                            String faultDescribe = currentGridFailure.getFaultDescribe();
                            if (StringUtils.isNotBlank(faultDescribe)) {
                               faultDescribeList.add(faultDescribe);
                            }
                        }
                        if (ObjectUtils.isNotEmpty(faultDescribeList)) {
                            result = "当前" + StringUtils.join(faultDescribeList, ",");
                            returnVo.setPoseId(tellHowCurrentGridFailureVO.getPoseId());
                            noticeTellHowAction(ResponseMessage.TellHowMenu.CURRENT_GRID_FAILURE);
                        }
                    }
                }
                if (StringUtils.isBlank(result)) {
                    result = "当前没有电网故障事件";
                }
                returnVo.setResults(result);
            } else if (serialNum == 312) {  // 保电信息查询
                String result = null;
                TellHowPowerSupplyInfoVO tellHowPowerSupplyInfoVO = this.tellHowClient.powerSupplyInfo(
                        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                if (null != tellHowPowerSupplyInfoVO) {
                    List<PowerSupplyInfo> powerSupplyInfoList = tellHowPowerSupplyInfoVO.getPowerSupplyInfoList();
                    if (ObjectUtils.isNotEmpty(powerSupplyInfoList)) {
                        List<String> taskNameList = new ArrayList<>();
                        for (PowerSupplyInfo powerSupplyInfo : powerSupplyInfoList) {
                            String taskName = powerSupplyInfo.getTaskName();
                            if (StringUtils.isNotBlank(taskName)) {
                                taskNameList.add(taskName);
                            }
                        }
                        if (ObjectUtils.isNotEmpty(taskNameList)) {
                            result = "当前保电任务有：" + StringUtils.join(taskNameList, ",");
                            returnVo.setPoseId(tellHowPowerSupplyInfoVO.getPoseId());
                            noticeTellHowAction(ResponseMessage.TellHowMenu.POWER_SUPPLY_INFO);
                        }
                    }
                }
                if (StringUtils.isBlank(result)) {
                    result = "当前没有保电任务";
                }
                returnVo.setResults(result);
            } else if (serialNum == 313) {  // 检修工作清单
                String result = null;
                List<OverhaulWorkList> overhaulWorkLists = new ArrayList<>();
                TellHowOverhaulWorkListVO tellHowOverhaulWorkListVO = this.tellHowClient.overhaulWorkList(
                        LocalDate.now(),
                        TellHowClient.OverhaulWorkListStatus.WAIT_FOR_START);
                if (null != tellHowOverhaulWorkListVO) {
                    List<OverhaulWorkList> workLists = tellHowOverhaulWorkListVO.getOverhaulWorkListList();
                    if (ObjectUtils.isNotEmpty(workLists)) {
                        overhaulWorkLists.addAll(workLists);
                        noticeTellHowAction(ResponseMessage.TellHowMenu.OVERHAUL_WORK_LIST_WAIT_FOR_START);
                    }
                }
                TellHowOverhaulWorkListVO tellHowOverhaulWorkListVOEXECUTION = this.tellHowClient.overhaulWorkList(
                        LocalDate.now(),
                        TellHowClient.OverhaulWorkListStatus.EXECUTING);
                if (null != tellHowOverhaulWorkListVOEXECUTION) {
                    List<OverhaulWorkList> workLists = tellHowOverhaulWorkListVOEXECUTION.getOverhaulWorkListList();
                    if (ObjectUtils.isNotEmpty(workLists)) {
                        overhaulWorkLists.addAll(workLists);
                        noticeTellHowAction(ResponseMessage.TellHowMenu.OVERHAUL_WORK_LIST_EXECUTING);
                    }
                }
                if (ObjectUtils.isNotEmpty(overhaulWorkLists)) {
                    result = "好的，已展示";
                    returnVo.setPoseId(tellHowOverhaulWorkListVO.getPoseId());
                    System.out.println(overhaulWorkLists);
                } else {
                    result = "当前没有检修任务";
                }
                returnVo.setResults(result);
            } else if (serialNum == 314) {  // 电网风险查询
                String result = null;
                TellHowGridRiskVO tellHowGridRiskVO = this.tellHowClient.gridRisk();
                if (null != tellHowGridRiskVO) {
                    List<GridRisk> gridRiskList = tellHowGridRiskVO.getGridRiskList();
                    if (ObjectUtils.isNotEmpty(gridRiskList)) {
                        List<String> gridRiskListStr = new ArrayList<>();
                        for (GridRisk gridRisk : gridRiskList) {
                            String gridRiskStr = gridRisk.getStationName() + "存在" + gridRisk.getEventLevel() + "电网风险";
                           gridRiskListStr.add(gridRiskStr);
                        }
                        if (ObjectUtils.isNotEmpty(gridRiskListStr)) {
                            result = StringUtils.join(gridRiskListStr, ",");
                            returnVo.setPoseId(tellHowGridRiskVO.getPoseId());
                            noticeTellHowAction(ResponseMessage.TellHowMenu.GRID_RISK);
                        }
                    }
                }
                if (StringUtils.isBlank(result)) {
                    result = "当前没有电网风险";
                }
                returnVo.setResults(result);
            } else if (serialNum == 315) {  // 获取地线
                String result = null;
                TellHowGroundWireVO tellHowGroundWireVO = this.tellHowClient.groundWire();
                if (null != tellHowGroundWireVO) {
                    List<GroundWire> groundWireList = tellHowGroundWireVO.getGroundWireList();
                    if (ObjectUtils.isNotEmpty(groundWireList)) {
                        List<String> groundWireListStr = new ArrayList<>();
                        for (GroundWire groundWire : groundWireList) {
                            groundWireListStr.add(groundWire.getGroundWireName());
                        }
                        if (ObjectUtils.isNotEmpty(groundWireListStr)) {
                            result = "存在地线：" + StringUtils.join(groundWireListStr, ",");
                            returnVo.setPoseId(tellHowGroundWireVO.getPoseId());
                            noticeTellHowAction(ResponseMessage.TellHowMenu.GROUND_WIRE);
                        }
                    }
                }
                if (StringUtils.isBlank(result)) {
                    result = "当前没有地线";
                }
                returnVo.setResults(result);
            } else if (serialNum == 316) {  // 值班人员信息
                TellHowVO result = null;
                if (regexIsFind("今天|今日", message)) {
                    result = this.tellHowClient.dutyPersonnelInfo(null, null);
                } else if (regexIsFind("昨天|昨日", message)) {
                    result = this.tellHowClient.dutyPersonnelInfo(LocalDate.now().minusDays(1), null);
                } else if (regexIsFind("明天|明日", message)) {
                    result = this.tellHowClient.dutyPersonnelInfo(LocalDate.now().plusDays(1), null);
                }
                if (null != result) {
                    String voiceContent = result.getVoiceContent();
                    if (StringUtils.isNotBlank(voiceContent)) {
                        returnVo.setResults(voiceContent);
                        returnVo.setPoseId(result.getPoseId());
                    }
                    noticeTellHowAction(ResponseMessage.TellHowMenu.DUTY_PERSONNEL_INFO);
                }
            } else if (serialNum == 317) {  // 当前的重过载设备情况
                String deviceInfo = this.nanRuiClient.getHeavyDevice();
                if (StringUtils.isNotBlank(deviceInfo)) {
                    returnVo.setResults(deviceInfo);
                }
            } else if (serialNum == 318) {  // 当前方式下是否有N-1过载设备
                String deviceInfo = this.nanRuiClient.getN1OverDevice();
                if (StringUtils.isNotBlank(deviceInfo)) {
                    returnVo.setResults(deviceInfo);
                }
            } else if (serialNum == 319) {  // 负荷走势预测
                if (regexIsFind("新能源", message)) {  // 新能源
                    TellHowLoadMovementNewEnergyVO tellHowLoadMovementNewEnergyVO = this.tellHowClient.loadMovementNewEnergy();
                    if (null != tellHowLoadMovementNewEnergyVO) {
                        returnVo.setResults("好的，已展示");
                        returnVo.setPoseId(tellHowLoadMovementNewEnergyVO.getPoseId());
                    }
                    noticeTellHowAction(ResponseMessage.TellHowMenu.LOAD_MOVEMENT_NEW_ENERGY);
                } else {    // 主网
                    TellHowLoadMovementMainNetVO tellHowLoadMovementMainNetVO = this.tellHowClient.loadMovementMainNet();
                    if (null != tellHowLoadMovementMainNetVO) {
                        returnVo.setResults("好的，已展示");
                        returnVo.setPoseId(tellHowLoadMovementMainNetVO.getPoseId());
                    }
                    noticeTellHowAction(ResponseMessage.TellHowMenu.LOAD_MOVEMENT_MAIN_NET);
                }
            } else {
                //获取数据库中信息
                InstructionSet issInformation = this.getById(serialNum);
                if (null != issInformation) {
                    if (issInformation.getStatus() == 0) {//返回替换[]后的数据
                        if (serialNum == 2) {//某日最高电量
                            List<AnnualElectricityConsumption> annualElectricityConsumptions = annualElectricityConsumptionService.getList(directive);
                            if (null != annualElectricityConsumptions && !annualElectricityConsumptions.isEmpty()) {
                                Double mathMaxDouble = annualElectricityConsumptions.stream()
                                        .mapToDouble(AnnualElectricityConsumption::getQuantityOfElectricity)
                                        .max()
                                        .getAsDouble();
                                returnVo.setResults(issInformation.getTemplateDesc().replace("[1]", mathMaxDouble.toString()));
                            }
                        }else if (serialNum == 34) {//全市[0]变电站数量为[1]
                            String dianya = directive;
                            dianya = dianya.substring(dianya.indexOf("个") + 1);
                            dianya = dianya.toUpperCase();
                            String d = "";
                            if (dianya.contains("千伏")) {
                                d = dianya.substring(0, dianya.lastIndexOf("千伏"));
                            }
                            if (dianya.contains("KV")) {
                                d = dianya.substring(0, dianya.lastIndexOf("KV"));
                            }
                            if (d.contains("亿") || d.contains("万") || d.contains("千") || d.contains("百") || d.contains("十")) {
                                d = String.valueOf(ArabicNumeralsUtil.zh2arbaNum(d));
                            }
                            if (!"".equals(d)) {
                                Long count = transformerService.getTransformer(d + "kV");
                                String result = issInformation.getTemplateDesc().replace("[0]",d);
                                result = result.replace("[1]",count.toString());
                                returnVo.setResults(result);
                            }
                        }
                    }
                    else if (issInformation.getStatus() == 1) { //直接返回templateDesc
                        returnVo.setResults(issInformation.getTemplateDesc());
                    }
                    else if (issInformation.getStatus() == 2) { //返回表格数据
                        returnVo.setResults("详见下表：");
                        if (serialNum == 1) {//某日电量
                            List<AnnualElectricityConsumption> annualElectricityConsumptions = annualElectricityConsumptionService.getList(directive);
                            if (null != annualElectricityConsumptions && !annualElectricityConsumptions.isEmpty()) {
                                returnVo.setTabData(Collections.singletonList(annualElectricityConsumptions));
                            }
                        } else if (serialNum == 13) {//任一母线电压曲线
                            String muxian = directive;
                            String d = "";
                            if (muxian.contains("母线")) {
                                d = muxian.substring(0, muxian.lastIndexOf("母线"));
                                d = d.toUpperCase();
                                if (d.contains("+")) {
                                    d = d.replace("+", " ");
                                }
                                if (d.contains("母")) {
                                    d = d.replace("母", "#");
                                }
                                if (d.contains("千伏")) {
                                    d = d.replace("千伏", "kV");
                                }
                                if (d.contains("亿") || d.contains("万") || d.contains("千") || d.contains("百") || d.contains("十")) {
                                    d = String.valueOf(ArabicNumeralsUtil.zh2arbaNum(d));
                                    d = d + "kV";
                                }
                                d = GeneratrixUtil.getText(d);
                                if (!(muxian.substring(muxian.lastIndexOf("母线") + 2).equals(""))) {
                                    d = d + muxian.substring(muxian.lastIndexOf("母线") + 2);
                                }
                                d = d + "母线";
                            }
                            if (!d.equals("")) {
                                List<Generatrix> generatrixList = generatrixService.getGeneratrix(d);
                                if (null != generatrixList && !generatrixList.isEmpty()) {
                                    List<Generatrix1> generatrixes = new ArrayList<>();
                                    for (Generatrix generatrix : generatrixList) {
                                        Generatrix1 generatrix1 = new Generatrix1();
                                        generatrix1.setName(generatrix.getName());
                                        generatrix1.setLineVoltage(generatrix.getLineVoltage().toString());
                                        generatrixes.add(generatrix1);
                                    }
                                    returnVo.setTabData(Collections.singletonList(generatrixes));
                                }
                            }

                        } else if (serialNum == 14) {//当前电压越限的母线
                            String dianya = directive;
                            dianya = dianya.toUpperCase();
                            String d = "";
                            if (dianya.contains("千伏")) {
                                d = dianya.substring(0, dianya.indexOf("千伏"));
                            } else if (dianya.contains("KV")) {
                                d = dianya.substring(0, dianya.indexOf("KV"));
                            }
                            if (d.contains("亿") || d.contains("万") || d.contains("千") || d.contains("百") || d.contains("十")) {
                                d = String.valueOf(ArabicNumeralsUtil.zh2arbaNum(d));
                            }
                            if (!d.equals("")) {
                                List<Generatrix> generatrixList = generatrixService.getGeneratrix2(d + "kv");
                                if (null != generatrixList && !generatrixList.isEmpty()) {
                                    List<Generatrix1> generatrix1s = new ArrayList<>();
                                    for (Generatrix generatrix : generatrixList) {
                                        Generatrix1 generatrix1 = new Generatrix1();
                                        generatrix1.setName(generatrix.getName());
                                        generatrix1.setLineVoltage(generatrix.getVoltageLevel());
                                        generatrix1s.add(generatrix1);
                                    }
                                    returnVo.setTabData(Collections.singletonList(generatrix1s));
                                }
                            }

                        } else if (serialNum == 15) {
                            String muxian = directive;
                            String d = "";
                            if (muxian.contains("母线")) {
                                d = muxian.substring(0, muxian.lastIndexOf("母线"));
                                d = d.toUpperCase();
                                if (d.contains("+")) {
                                    d = d.replace("+", " ");
                                }
                                if (d.contains("母")) {
                                    d = d.replace("母", "#");
                                }
                                if (d.contains("千伏")) {
                                    d = d.replace("千伏", "kV");
                                }
                                if (d.contains("亿") || d.contains("万") || d.contains("千") || d.contains("百") || d.contains("十")) {
                                    d = String.valueOf(ArabicNumeralsUtil.zh2arbaNum(d));
                                    d = d + "kV";
                                }
                                d = GeneratrixUtil.getText(d);
                                if (!(muxian.substring(muxian.lastIndexOf("母线") + 2).equals(""))) {
                                    d = d + muxian.substring(muxian.lastIndexOf("母线") + 2);
                                }
                                d = d + "母线";
                            }
                            if (!d.equals("")) {
                                List<Generatrix> generatrixList = generatrixService.getGeneratrix(d);
                                List<Generatrix3> generatrix3s = new ArrayList<>();
                                if (null != generatrixList && !generatrixList.isEmpty()) {
                                    for (Generatrix generatrix : generatrixList) {
                                        Generatrix3 generatrix3 = new Generatrix3();
                                        generatrix3.setName(generatrix.getName());
                                        generatrix3.setLowerVoltageLimit(generatrix.getLowerVoltageLimit());
                                        generatrix3.setUpperVoltageLimit(generatrix.getUpperVoltageLimit());
                                        generatrix3s.add(generatrix3);
                                    }
                                    returnVo.setTabData(Collections.singletonList(generatrix3s));
                                }
                            }

                        } else if (serialNum == 22) {//任一站失压风险分析
                            String zhan = directive;
                            zhan = zhan.substring(0, zhan.lastIndexOf("站"));
                            if (!zhan.equals("")) {
                                List<RiskAnalysis> riskAnalysisList = riskAnalysisService.getRiskAnalysis(zhan);
                                if (null != riskAnalysisList && !riskAnalysisList.isEmpty()) {
                                    returnVo.setTabData(Collections.singletonList(riskAnalysisList));
                                }
                            }
                        } else if (serialNum == 36) {//X月X日电量是多少
                            String time = directive;
                            List<AnnualElectricityConsumption> annualElectricityConsumptions = annualElectricityConsumptionService.getList(time);
                            if (null != annualElectricityConsumptions && !annualElectricityConsumptions.isEmpty()) {
                                returnVo.setTabData(Collections.singletonList(annualElectricityConsumptions));
                            }
                        }
                    }
                    else if (issInformation.getStatus() == 3) {//表＋拼接
                        if (serialNum == 10) { //各电压等级线路总长度
                            String dianya = directive;
                            dianya = dianya.toUpperCase();
                            String d = "";
                            if (dianya.contains("千伏电压")) {
                                d = dianya.substring(0, dianya.lastIndexOf("千伏电压"));
                            }
                            if (dianya.contains("KV")) {
                                d = dianya.substring(0, dianya.lastIndexOf("KV"));
                            }
                            if (d.contains("亿") || d.contains("万") || d.contains("千") || d.contains("百") || d.contains("十")) {
                                d = String.valueOf(ArabicNumeralsUtil.zh2arbaNum(d));
                            }
                            if (!d.equals("")) {
                                List<Line> lineList = lineService.getLine(d + "kV");
                                if (!lineList.isEmpty()) {
                                    returnVo.setTabData(Collections.singletonList(lineList));
                                    Double len = lineList.stream().mapToDouble(p -> p.getLineLength()).sum();
                                    returnVo.setResults(issInformation.getTemplateDesc().replace("[1]", len.toString()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void noticeTellHowAction(ResponseMessage.TellHowMenu totalLoadCurve) {
        ResponseMessage tellHowResponseMessage = new ResponseMessage(null,
                ServiceType.ZHIBIAO,
                null,
                null);
        tellHowResponseMessage.setResult(null);
        tellHowResponseMessage.setMenuCode(totalLoadCurve);
        TellHowServer.noticeClient2(tellHowResponseMessage);
    }

    //处理图片信息
    private void haveJpgPath(InterlocutionResult ilResult, ReturnVo returnVo) {
        String question = ilResult.getQuestion();
        if (question.contains("负荷")) {
            returnVo.setJpgPath("/home/jpgs/dh.png");
        }
        if (question.contains("风险分析")) {
            returnVo.setJpgPath("/home/jpgs/fxfx.png");
        }
        if (question.contains("值班")) {
            returnVo.setJpgPath("/home/jpgs/zbry.png");
        }
        if (question.contains("负载率")) {
            returnVo.setJpgPath("/home/jpgs/fzlqs.png");
        }
        if (question.contains("主网检修计划")) {
            returnVo.setJpgPath("/home/jpgs/jxjh1.png");
        }
    }

    /**
     * 判断是否匹配
     *
     * @param regex 正则表达式
     * @param text 待匹配文本
     * @return 匹配结果
     */
    private Boolean regexIsFind(String regex, String text) {
        return com.example.database.fanyumeta.utils.StringUtils.regexIsFind(regex, text);
    }

    /**
     * 设置比较值
     *
     * @param tellHowCurveVO 泰豪负荷曲线值对象
     * @param area 区域
     */
    private void setCompareValue(TellHowCurveVO tellHowCurveVO, TellHowClient.Area area) {
        LocalDate yesterdayDate = LocalDate.now().minusDays(1);
        LocalDate lastYearDate = LocalDate.now().minusYears(1);
        List<MaxLoad> maxLoadList = null;
        if (area == null) {
            maxLoadList = this.loadService.getTotalLoadByRecordDate(yesterdayDate, lastYearDate);
        } else {
            maxLoadList = this.loadService.getZoneLoadByRecordDate(area.getValue(), yesterdayDate, lastYearDate);
        }
        if (ObjectUtils.isNotEmpty(maxLoadList)) {
            String currentMaxValue = tellHowCurveVO.getDateMaxValue();
            for (MaxLoad totalLoad : maxLoadList) {
                String historyMaxValue = totalLoad.getDateMaxValue();
                String compareValue = this.calcCompare(currentMaxValue, historyMaxValue);
                if (StringUtils.isNotBlank(compareValue)) {
                    if (totalLoad.getRecordDate().equals(yesterdayDate)) {
                        tellHowCurveVO.setCompareYesterday(compareValue);
                    } else if (totalLoad.getRecordDate().equals(lastYearDate)) {
                        tellHowCurveVO.setCompareLastYear(compareValue);
                    }
                }
            }
        }
    }

    /**
     * 计算比较值
     *
     * @param currentMaxValue 当前最大值
     * @param historyMaxValue 历史最大值
     * @return 比较结果（%）
     */
    private String calcCompare(String currentMaxValue, String historyMaxValue) {
        String result = null;
        try {
            BigDecimal currentValue = new BigDecimal(currentMaxValue);
            BigDecimal historyValue = new BigDecimal(historyMaxValue);
            BigDecimal compareValue = currentValue.subtract(historyValue).divide(historyValue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
            result = compareValue.toString();
        } catch (Exception e) {
            log.error("currentMaxValue：{}，historyMaxValue：{}，计算比较值异常", currentMaxValue, historyMaxValue, e);
        }
        return result;
    }

    /**
     * 获取区域
     *
     * @param question 问题文本
     * @return 区域
     */
    private TellHowClient.Area getArea(String question) {
        TellHowClient.Area area = null;
        if (regexIsFind("安新", question)) {
            area = TellHowClient.Area.AN_XIN;
        } else if (regexIsFind("容城", question)) {
            area = TellHowClient.Area.RONG_CHENG;
        } else if (regexIsFind("雄县县城", question)) {
            area = TellHowClient.Area.XIONG_XIAN;
        } else if (regexIsFind("沧州片区", question)) {
            area = TellHowClient.Area.CANG_ZHOU;
        } else if (regexIsFind("容东", question)) {
            area = TellHowClient.Area.RONG_DONG;
        } else if (regexIsFind("容西", question)) {
            area = TellHowClient.Area.RONG_XI;
        } else if (regexIsFind("雄东", question)) {
            area = TellHowClient.Area.XIONG_DONG;
        } else if (regexIsFind("启动", question)) {
            area = TellHowClient.Area.QI_DONG;
        } else if (regexIsFind("目标电网", question)) {
            area = TellHowClient.Area.MU_BIAO;
        } else if (regexIsFind("雄县.*沧州.*", question)) {
            area = TellHowClient.Area.XIONG_XIAN_CANG_ZHOU;
        }
        return area;
    }
}




