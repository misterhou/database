package com.example.database.fanyumeta.client;

import com.example.database.contant.MyContants;
import com.example.database.fanyumeta.handler.HardwareHandler;
import com.example.database.fanyumeta.socket.TCPClient;
import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 中控服务客户端
 */
@Slf4j
@Component
@EnableConfigurationProperties(HardwareControlProperties.class)
public class HardwareControlClient {
    private HardwareControlProperties hardwareControlProperties;

    @Autowired
    public HardwareControlClient(HardwareControlProperties hardwareControlProperties) {
        this.hardwareControlProperties = hardwareControlProperties;
    }

//    /**
//     * 发送消息给中控服务
//     *
//     * @param message 消息内容
//     */
//    public void sendMessage(String message) {
//        List<String> commandList = getHardwareCommand(message);
//        for (String command : commandList) {
//            this.sendCommand(command);
//        }
//    }

    /**
     * 发送消息给中控服务
     *
     * @param message 消息内容
     */
    public String sendMessage(String message) {
        if (this.hardwareControlProperties.getEnabled()) {
            List<String> commandList = getHardwareCommand(message);
            if (!ObjectUtils.isEmpty(commandList)) {
                return this.sendCommand(commandList.get(0));
            } else {
                return null;
            }
        } else {
            return MyContants.YX_ZL_ANS;
        }
    }

    /**
     * 发送指定数据到中控系统
     * @param command 中控指令
     */
    @Deprecated
    public String sendCommand(String command) {
        try {
            HardwareHandler hardwareHandler = new HardwareHandler();
            TCPClient client = new TCPClient(this.hardwareControlProperties.getHost(),
                    this.hardwareControlProperties.getPort(), hardwareHandler);
            client.sendMessage(command);
            log.info("【发送给中控】的消息：{}", command);
            Thread.sleep(this.hardwareControlProperties.getTimeout() * 1000);
            client.close();
            return HardwareControlCommandUtil.getReceiveCommandDescription(hardwareHandler.getMessage());
        } catch (Exception e) {
            String errMsg = "发送指令到中控系统出错";
            log.error(errMsg, e);
            return null;
//            throw new RuntimeException(errMsg);
        }
    }

    /**
     * 通过文字获取中控指令集合
     * @param message 文字信息
     * @return 中控指令集合
     */
    private List<String> getHardwareCommand(String message) {
        List<String> command = HardwareControlCommandUtil.parse(message);
        if (log.isDebugEnabled()) {
            log.debug("将【{}】通过系统解析后得到的中控指令【{}】", message, command);
        }
        return command;
    }
}
