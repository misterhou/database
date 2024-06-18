package com.example.database.fanyumeta.controller;

import com.example.database.fanyumeta.client.HardwareControlClient;
import com.example.database.fanyumeta.server.TellHowServer;
import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

}