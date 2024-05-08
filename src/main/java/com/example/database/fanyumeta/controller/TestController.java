package com.example.database.fanyumeta.controller;

import com.example.database.fanyumeta.client.HardwareControlClient;
import com.example.database.fanyumeta.server.TellHowServer;
import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {

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

    @PostMapping("/imitate/8017")
    public String imitate8017(String message) {
        return "{\"id\": \"39\", \"question\": \"\\u5927\\u5c4f\\u524d\\u897f\\u4fa7\\u7a7a\\u8c03\\u6253\\u5f00\", \"cate\": \"\\u628a\\u5927\\u5c4f\\u524d\\u897f\\u4fa7/\\u4e1c\\u4fa7\\u7a7a\\u8c03\\u6e29\\u5ea6\\u8c03\\u523023\\u5ea6\", \"directiveType\": \"\\u8fd0\\u884c\\u6307\\u4ee4\", \"directive\": \"\\u65e0\"}";
    }

    @GetMapping("/update/command/cache")
    public String updateCommandCache() {
        HardwareControlCommandUtil.initCache(
                this.requestCommandConfigFile,
                this.receiveCommandConfigFile);
        return "success";
    }

    @PostMapping("/imitate/large/model")
    public String imitateLargeModel(String message) {
        return "{\"answer\":\"问题答案\",\"docs\":[]}";
    }

    @GetMapping("/tell-how/send/{message}")
    public String sendMessage2Client(@PathVariable String message) {
        TellHowServer.noticeClient(message, null);
        return "success";
    }

}
