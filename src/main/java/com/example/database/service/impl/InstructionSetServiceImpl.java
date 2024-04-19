package com.example.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.contant.MyContants;
import com.example.database.domain.*;
import com.example.database.entity.*;
import com.example.database.service.*;
import com.example.database.mapper.InstructionSetMapper;
import com.example.database.utils.ArabicNumeralsUtil;
import com.example.database.utils.GeneratrixUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author woshi
 * @description 针对表【instruction_set】的数据库操作Service实现
 * @createDate 2023-09-20 09:56:26
 */
@Service
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


    public String getEnv() {
        return this.env.getProperty("interlocutionUrl");
    }

    @Override
    public void haveReturnVo(InterlocutionResult ilResult, ReturnVo returnVo) {
        int serialNum = Integer.valueOf(ilResult.getId());
        String directive = ilResult.getDirective();
        String directiveType = ilResult.getDirectiveType();
        returnVo.setId(ilResult.getId());
        //运行指令
        if (MyContants.YX_ZL.equals(directiveType)) {
            returnVo.setResults(MyContants.YX_ZL_ANS);
        }
        //处理图片信息
        this.haveJpgPath(ilResult, returnVo);
        //数据库指令
        if (MyContants.SJK_ZL.equals(directiveType)) {
            //获取数据库中信息
            InstructionSet issInformation = this.getById(serialNum);
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
}




