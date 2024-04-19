package com.example.database.fanyumeta.controller;

import com.example.database.fanyumeta.client.HardwareControlClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test/hardware")
public class HardwareController {

    @Resource
    private HardwareControlClient hardwareControlClient;

    @GetMapping("/send/message")
    public String sendMessage(String message) {
        this.hardwareControlClient.sendMessage(message);
        return "success";
    }

    @GetMapping("/send/command")
    public String sendCommon(String message) {
        return this.hardwareControlClient.sendCommand(message);
    }
}
