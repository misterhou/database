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
import com.example.database.fanyumeta.client.TellHowClient;
import com.example.database.fanyumeta.client.vo.TellHowCurveVO;
import com.example.database.fanyumeta.client.vo.TellHowTransLoadRateVO;
import com.example.database.fanyumeta.client.vo.TellHowVO;
import com.example.database.fanyumeta.client.vo.TransLoadRate;
import com.example.database.fanyumeta.server.ServiceType;
import com.example.database.fanyumeta.server.TellHowServer;
import com.example.database.fanyumeta.server.tellhow.ResponseMessage;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
     * 中控系统客户端
     */
    @Resource
    private HardwareControlClient hardwareControlClient;

    /**
     * 泰豪服务客户端
     */
    @Resource
    private TellHowClient tellHowClient;

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
            if (serialNum == 35 || serialNum == 103) {
                if (regexIsFind("值班", message)) {
                    TellHowVO result = this.tellHowClient.dutyPersonnelInfo(null, "夜班");
                    if (null != result) {
                        String voiceContent = result.getVoiceContent();
                        if (StringUtils.isNotBlank(voiceContent)) {
                            returnVo.setResults(voiceContent);
                            returnVo.setPoseId(result.getPoseId());
                        }
                        noticeTellHowAction(ResponseMessage.TellHowMenu.DUTY_PERSONNEL_INFO);
                    }
                } else if (regexIsFind("负荷", message)) {
                    if (regexIsFind("今日|今天", message)) {
                        TellHowClient.Area area = null;
                        ResponseMessage.TellHowMenu menu = null;
                        if (regexIsFind("安新", message)) {
                            area = TellHowClient.Area.AN_XIN;
                            menu = ResponseMessage.TellHowMenu.AN_XIN_LOAD_CURVE;
                        } else if (regexIsFind("容城", message)) {
                            area = TellHowClient.Area.RONG_CHENG;
                            menu = ResponseMessage.TellHowMenu.RONG_CHENG_LOAD_CURVE;
                        } else if (regexIsFind("雄县县城", message)) {
                            area = TellHowClient.Area.XIONG_XIAN;
                            menu = ResponseMessage.TellHowMenu.XIONG_XIAN_LOAD_CURVE;
                        } else if (regexIsFind("沧州片区", message)) {
                            area = TellHowClient.Area.CANG_ZHOU;
                            menu = ResponseMessage.TellHowMenu.CANG_ZHOU_LOAD_CURVE;
                        } else if (regexIsFind("容东", message)) {
                            area = TellHowClient.Area.RONG_DONG;
                            menu = ResponseMessage.TellHowMenu.RONG_DONG_LOAD_CURVE;
                        } else if (regexIsFind("容西", message)) {
                            area = TellHowClient.Area.RONG_XI;
                            menu = ResponseMessage.TellHowMenu.RONG_XI_LOAD_CURVE;
                        } else if (regexIsFind("雄东", message)) {
                            area = TellHowClient.Area.XIONG_DONG;
                            menu = ResponseMessage.TellHowMenu.XIONG_DONG_LOAD_CURVE;
                        } else if (regexIsFind("启动", message)) {
                            area = TellHowClient.Area.QI_DONG;
                            menu = ResponseMessage.TellHowMenu.QI_DONG_LOAD_CURVE;
                        } else if (regexIsFind("目标电网", message)) {
                            area = TellHowClient.Area.MU_BIAO;
                            menu = ResponseMessage.TellHowMenu.MU_BIAO_LOAD_CURVE;
                        } else if (regexIsFind("雄县.*沧州.*", message)) {
                            area = TellHowClient.Area.XIONG_XIAN_CANG_ZHOU;
                            menu = ResponseMessage.TellHowMenu.XIONG_XIAN_CANG_ZHOU_LOAD_CURVE;
                        }
                        TellHowCurveVO tellHowCurveVO = null;
                        if (null != area) {
                            tellHowCurveVO = this.tellHowClient.zoneLoadCurve(LocalDate.now(), area);
                        } else {
                            tellHowCurveVO = this.tellHowClient.totalLoadCurve(LocalDate.now());
                            menu = ResponseMessage.TellHowMenu.TOTAL_LOAD_CURVE;
                        }
                        if (null != tellHowCurveVO) {
                            String maxValue = tellHowCurveVO.getDateMaxValue();
                            if (StringUtils.isNotBlank(maxValue)) {
                                returnVo.setResults(message.replace("多少", maxValue + "MW"));
                                String poseId = tellHowCurveVO.getPoseId();
                                if (StringUtils.isNotBlank(poseId)) {
                                    returnVo.setPoseId(poseId);
                                }
                                noticeTellHowAction(menu);
                            }
                        }
                    }
                }
            } else if (serialNum == 307) {
                String text = message.replaceAll("(负载率|是多少)", "");
                TellHowTransLoadRateVO tellHowTransLoadRateVO = this.tellHowClient.transLoadRate();
                if (null != tellHowTransLoadRateVO) {
                    List<TransLoadRate> transLoadRateList = tellHowTransLoadRateVO.getTransLoadRateList();
                    if (ObjectUtils.isNotEmpty(transLoadRateList)) {
                        for (TransLoadRate transLoadRate : transLoadRateList) {
                            String fullName = transLoadRate.getFullName();
                            if (StringUtils.isNotBlank(fullName)) {
                                double similarity = SimilarityUtil.getSimilarity(fullName, text);
                                log.info("【相似度】【{}】与【{}】的为：{}", fullName, text, similarity);
                                if (similarity > 0.8) {
                                    StringBuilder rate = new StringBuilder();
                                    rate.append(transLoadRate.getRealtimeRate())
                                            .append(", 最大负载率是").append(transLoadRate.getMaxRate());
                                    String result = message.replaceAll("多少", rate.toString());
                                    returnVo.setResults(result);
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
}




