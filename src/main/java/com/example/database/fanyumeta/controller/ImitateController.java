package com.example.database.fanyumeta.controller;

import com.example.database.fanyumeta.client.HardwareControlClient;
import com.example.database.fanyumeta.server.TellHowServer;
import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import com.example.database.fanyumeta.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/imitate")
public class ImitateController {

    @Resource
    private HardwareControlClient hardwareControlClient;

    @Value("${fan-yu.hardware-control.request-command-config-file}")
    private String requestCommandConfigFile;

    @Value("${fan-yu.hardware-control.receive-command-config-file}")
    private String receiveCommandConfigFile;

    @GetMapping("/hardware/send/message")
    public String sendMessage(String message) {
        this.hardwareControlClient.sendMessage(message);
        return "success";
    }

    @GetMapping("/hardware/send/command")
    public String sendCommon(String message) {
        return this.hardwareControlClient.sendCommand(message);
    }

    @PostMapping("/8017")
    public String imitate8017(String message) {
        return "{\"id\": \"10000\", \"question\": \"\\u5927\\u5c4f\\u524d\\u897f\\u4fa7\\u7a7a\\u8c03\\u6253\\u5f00\", \"cate\": \"\\u628a\\u5927\\u5c4f\\u524d\\u897f\\u4fa7/\\u4e1c\\u4fa7\\u7a7a\\u8c03\\u6e29\\u5ea6\\u8c03\\u523023\\u5ea6\", \"directiveType\": \"数据库查询指令\", \"directive\": \"\\u65e0\"}";
    }

    @GetMapping("/update/command/cache")
    public String updateCommandCache() {
        HardwareControlCommandUtil.initCache(
                this.requestCommandConfigFile,
                this.receiveCommandConfigFile);
        return "success";
    }

    @PostMapping("/large/model")
    public String imitateLargeModel(String message) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{\"answer\":\"小杨变电站端设备应满足Q/GDW 231、Q/GDW 383、Q/GDW 678、Q/GDW 679等一系列标准的相关要求，这些要求包括通用要求、一次设备技术要求和变电站监控系统技术要求。具体来说，需要满足遥控操作的开关、刀闸、主变有载调压分接头等设备的电动控制功能，图形描述、设备命名、数据、模型、图形等信息交互需要满足相应标准的要求，同时应具备流程管控功能、远方操作功能等。此外，变电站端设备还需要支持向调控主站提供各类运行数据，支持告警直传和远程浏览功能，支持变电站内设备就地和远方的操作控制。\",\"docs\":[\"出处 [1] [9调度控制远方操作技术规范.txt](http://192.162.1.117:7861/knowledge_base/download_doc?knowledge_base_name=diaodu_yuancheng&file_name=9%E8%B0%83%E5%BA%A6%E6%8E%A7%E5%88%B6%E8%BF%9C%E6%96%B9%E6%93%8D%E4%BD%9C%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83.txt) \\n\\n6 变电站端技术要求\\n\\n6.1 通用要求\\n\\n变电站端设备应满足Q/GDW 231、Q/GDW 383、Q/GDW 678、Q/GDW 679、Q/GDW 1161、Q/GDW 1175 、Q/GDW 1396、Q/GDW 10766 、Q/GDW 10767 、Q/GDW 11010、Q/GDW 11417等标准的相关要求。\\n\\n6.2 一次设备技术要求\\n\\n6.2.1 需遥控操作的开关、刀闸、主变有载调压分接头等应具备电动控制功能。\\n\\n\",\"出处 [2] [9调度控制远方操作技术规范.txt](http://192.162.1.117:7861/knowledge_base/download_doc?knowledge_base_name=diaodu_yuancheng&file_name=9%E8%B0%83%E5%BA%A6%E6%8E%A7%E5%88%B6%E8%BF%9C%E6%96%B9%E6%93%8D%E4%BD%9C%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83.txt) \\n\\n求，图形描述应满足 DL/T 1230 要求，设备命名应满足DL/T 1171 要求；数据、模型、图形等信息交\\n\\n互应满足DL/T 1169、DL/T 1170、DL/T 1232、DL/T 1233 要求；应具备流程管控功能，实现调控远方\\n\\n操作的流程全过程管控。\\n\\n4.3.2 变电站端设备应满足Q/GDW 231、Q/GDW 678、Q/GDW 679 等标准的相关要求。\\n\\n4.3.3 继电保护和安全自动装置远方操作在调控主站端依托电网调度控制系统集中监控功能模块实\\n\\n\",\"出处 [3] [9调度控制远方操作技术规范.txt](http://192.162.1.117:7861/knowledge_base/download_doc?knowledge_base_name=diaodu_yuancheng&file_name=9%E8%B0%83%E5%BA%A6%E6%8E%A7%E5%88%B6%E8%BF%9C%E6%96%B9%E6%93%8D%E4%BD%9C%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83.txt) \\n\\n6.2.4 刀闸应具备机械闭锁功能或电气闭锁功能。\\n\\n6.3 变电站监控系统技术要求\\n\\n6.3.1 应支持向调控主站提供设备运行数据和保护信息，一、二次设备运行状态信息，装置软压板及定值信息，二次回路及通信状态信息等变电站运行数据。\\n\\n6.3.2 应支持告警直传和远程浏览功能，图形文件格式应符合DL/T 1230的要求。\\n\\n6.3.3 应支持变电站内设备就地和远方的操作控制，包括开关/刀闸操作、二次设备软压板投退、定值区的远方切换。\\n\\n\"]}";
    }

    @GetMapping("/tell-how/send/{message}")
    public String sendMessage2Client(@PathVariable String message) {
        TellHowServer.noticeClient(message, null);
        return "success";
    }

    @GetMapping("/tell-how/admin/data/dutyPersonnelInfo")
    public String dutyPersonnelInfo(String dateTime, String dutyOrderName, String actionType) {
        System.out.println("dateTime: " + dateTime + ", dutyOrderName: " + dutyOrderName + ", actionType: " + actionType);
        String data = "{\n" +
                "    \"code\": 0,\n" +
                "    \"bizCode\": null,\n" +
                "    \"msg\": null,\n" +
                "    \"data\": {\n" +
                "        \"resData\": [\n" +
                "            {\n" +
                "                \"personnelName\": \"韩松原\",\n" +
                "                \"personnelIdentity\": \"值长\",\n" +
                "                \"personnelPhoto\": \"664568d484aebd7b45a3d212\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"personnelName\": \"赵学良\",\n" +
                "                \"personnelIdentity\": \"正值\",\n" +
                "                \"personnelPhoto\": \"664568d484aebd7b45a3d212\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"personnelName\": \"康奇豹\",\n" +
                "                \"personnelIdentity\": \"副值\",\n" +
                "                \"personnelPhoto\": \"664568d484aebd7b45a3d212\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"personnelName\": \"邱林涛\",\n" +
                "                \"personnelIdentity\": \"调度跟班\",\n" +
                "                \"personnelPhoto\": \"664568d484aebd7b45a3d212\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"actionData\": {\n" +
                "            \"poseId\": \"3\",\n" +
                "            \"actionType\": \"3\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        return data;
    }

    @GetMapping("/tell-how/data/totalLoadCurve")
    public String totalLoadCurve(String dateTime, String actionType) {
        return "{\n" +
                "    \"code\": 0,\n" +
                "    \"bizCode\": null,\n" +
                "    \"msg\": null,\n" +
                "    \"data\": {\n" +
                "        \"resData\": {\n" +
                "            \"minValue\": 405.06,\n" +
                "            \"yesterdayCurve\": {\n" +
                "                \"xData\": [\n" +
                "                    \"2024-06-04 00:00\",\n" +
                "                    \"2024-06-04 00:01\",\n" +
                "                    \"2024-06-04 23:58\",\n" +
                "                    \"2024-06-04 23:59\"\n" +
                "                ],\n" +
                "                \"yData\": [\n" +
                "                    476.4,\n" +
                "                    474.12,\n" +
                "                    488.06,\n" +
                "                    487.14\n" +
                "                ]\n" +
                "            },\n" +
                "            \"todayCurve\": {\n" +
                "                \"min\": 405.06,\n" +
                "                \"max\": 785.02,\n" +
                "                \"xData\": [\n" +
                "                    \"2024-06-05 00:00\",\n" +
                "                    \"2024-06-05 00:01\",\n" +
                "                    \"2024-06-05 23:58\",\n" +
                "                    \"2024-06-05 23:59\"\n" +
                "                ],\n" +
                "                \"yData\": [\n" +
                "                    486.71,\n" +
                "                    484.02,\n" +
                "                    482.88,\n" +
                "                    482.87\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        \"actionData\": {\n" +
                "            \"poseId\": \"1\",\n" +
                "            \"actionType\": \"3\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    /**
     * 模拟泰豪雄安分片区负荷曲线
     * @param dateTime 查询时间
     * @param actionType 动作类型
     * @param area 区域
     * @return 负荷曲线
     */
    @GetMapping("/tell-how/data/zoneLoadCurve")
    public String zoneLoadCurve(String dateTime, String actionType, Integer area) {
        return "{\n" +
                "    \"code\": 0,\n" +
                "    \"bizCode\": null,\n" +
                "    \"msg\": null,\n" +
                "    \"data\": {\n" +
                "        \"resData\": {\n" +
                "            \"minValue\": 12.84,\n" +
                "            \"yesterdayCurve\": {\n" +
                "                \"xData\": [\n" +
                "                    \"2024-06-04 00:00\",\n" +
                "                    \"2024-06-04 00:05\",\n" +
                "                    \"2024-06-04 23:50\",\n" +
                "                    \"2024-06-04 23:55\"\n" +
                "                ],\n" +
                "                \"yData\": [\n" +
                "                    16.98,\n" +
                "                    16.31,\n" +
                "                    18.26,\n" +
                "                    18.16\n" +
                "                ]\n" +
                "            },\n" +
                "            \"todayCurve\": {\n" +
                "                \"min\": 12.84,\n" +
                "                \"max\": 52.78,\n" +
                "                \"xData\": [\n" +
                "                    \"2024-06-05 00:00\",\n" +
                "                    \"2024-06-05 00:05\",\n" +
                "                    \"2024-06-05 23:50\",\n" +
                "                    \"2024-06-05 23:55\"\n" +
                "                ],\n" +
                "                \"yData\": [\n" +
                "                    18.16,\n" +
                "                    17.36,\n" +
                "                    17.21,\n" +
                "                    17.36\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        \"actionData\": {\n" +
                "            \"poseId\": \"1\",\n" +
                "            \"actionType\": \"3\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @GetMapping("/tell-how/data/transLoadRate")
    public String transLoadRate(String actionType) {
        return "{\n" +
                "    \"code\": 0,\n" +
                "    \"bizCode\": null,\n" +
                "    \"msg\": null,\n" +
                "    \"data\": {\n" +
                "        \"resData\": [\n" +
                "            {\n" +
                "                \"number\": 1,\n" +
                "                \"id\": null,\n" +
                "                \"devName\": \"#1 主变-中\",\n" +
                "                \"stName\": \"剧村站\",\n" +
                "                \"maxRate\": 100.3,\n" +
                "                \"realtimeRate\": 70.2\n" +
                "            }\n" +
                "        ],\n" +
                "        \"actionData\": {\n" +
                "            \"poseId\": \"3\",\n" +
                "            \"actionType\": \"3\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @GetMapping("/tell-how/data/lineLoadRate")
    public String lineLoadRate(String actionType) {
        return "{\n" +
                "    \"code\": 0,\n" +
                "    \"bizCode\": null,\n" +
                "    \"msg\": null,\n" +
                "    \"data\": {\n" +
                "        \"resData\": [\n" +
                "            {\n" +
                "                \"number\": 1,\n" +
                "                \"id\": null,\n" +
                "                \"devName\": \"平仇线端点\",\n" +
                "                \"maxRate\": null,\n" +
                "                \"realtimeRate\": 72\n" +
                "            },\n" +
                "            {\n" +
                "                \"number\": 2,\n" +
                "                \"id\": null,\n" +
                "                \"devName\": \"平仇线端点\",\n" +
                "                \"maxRate\": null,\n" +
                "                \"realtimeRate\": 71\n" +
                "            },\n" +
                "            {\n" +
                "                \"number\": 3,\n" +
                "                \"id\": null,\n" +
                "                \"devName\": \"河北.剧村站/10kV.剧村A1-I线线端\",\n" +
                "                \"maxRate\": 24.41,\n" +
                "                \"realtimeRate\": 70\n" +
                "            },\n" +
                "            {\n" +
                "                \"number\": 4,\n" +
                "                \"id\": null,\n" +
                "                \"devName\": \"瀛武Ⅱ线端点\",\n" +
                "                \"maxRate\": null,\n" +
                "                \"realtimeRate\": 68\n" +
                "            },\n" +
                "            {\n" +
                "                \"number\": 5,\n" +
                "                \"id\": null,\n" +
                "                \"devName\": \"河北.剧村站/10kV.剧剧Ⅰ线线端\",\n" +
                "                \"maxRate\": 5.46,\n" +
                "                \"realtimeRate\": 67\n" +
                "            }\n" +
                "        ],\n" +
                "        \"actionData\": {\n" +
                "            \"poseId\": \"3\",\n" +
                "            \"actionType\": \"3\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @GetMapping("/tell-how/num/numMinusOneDetails")
    public String numMinusOneDetails(String actionType) {
        return "{\n" +
                "    \"code\": 0,\n" +
                "    \"bizCode\": null,\n" +
                "    \"msg\": null,\n" +
                "    \"data\": {\n" +
                "        \"resData\": [\n" +
                "            {\n" +
                "                \"childrenItem\": [\n" +
                "                    {\n" +
                "                        \"number\": 1,\n" +
                "                        \"devName\": \"2#主变-中\",\n" +
                "                        \"stName\": \"白洋淀站\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"number\": 2,\n" +
                "                        \"devName\": \"2#主变-高\",\n" +
                "                        \"stName\": \"白洋淀站\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"number\": 3,\n" +
                "                        \"devName\": \"1#主变-中\",\n" +
                "                        \"stName\": \"容城站\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"number\": 4,\n" +
                "                        \"devName\": \"1#主变-高\",\n" +
                "                        \"stName\": \"容城站\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"number\": 5,\n" +
                "                        \"devName\": \"2#主变-中\",\n" +
                "                        \"stName\": \"容城站\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"number\": 6,\n" +
                "                        \"devName\": \"2#主变-高\",\n" +
                "                        \"stName\": \"容城站\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"number\": 7,\n" +
                "                        \"devName\": \"三台站/110kV.#2主变-高\",\n" +
                "                        \"stName\": \"三台站\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"number\": 8,\n" +
                "                        \"devName\": \"三台站/10kV.#2主变-低\",\n" +
                "                        \"stName\": \"三台站\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"childrenItem\": [\n" +
                "                    {\n" +
                "                        \"number\": 9,\n" +
                "                        \"devName\": \"新安站/110kV.#1主变-高\",\n" +
                "                        \"stName\": \"新安站\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"actionData\": {\n" +
                "            \"poseId\": \"1\",\n" +
                "            \"actionType\": \"3\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    @GetMapping("/pinyin/{text}")
    public String pinyin(@PathVariable("text") String text) {
        return StringUtils.getPinyin(text);
    }

}
