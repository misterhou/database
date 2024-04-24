package com.example.database.fanyumeta.client;

import com.example.database.contant.MyContants;
import com.example.database.fanyumeta.handler.HardwareHandler;
import com.example.database.fanyumeta.socket.TCPClient;
import com.example.database.fanyumeta.utils.HardwareControlCommandUtil;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Promise;
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

    /**
     * 发送消息给中控服务
     *
     * @param message 消息内容
     */
    public String sendMessage(String message) {
        if (this.hardwareControlProperties.getEnabled()) {
            String command = getHardwareCommand2(message);
            if (!ObjectUtils.isEmpty(command)) {
                return this.sendCommand(command);
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
            NioEventLoopGroup handlerEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("tcp-res-msg"));
            Promise<String> promise = new DefaultPromise<>(handlerEventLoopGroup.next());
            HardwareHandler hardwareHandler = new HardwareHandler(promise);
            TCPClient client = new TCPClient(this.hardwareControlProperties.getHost(),
                    this.hardwareControlProperties.getPort(), hardwareHandler);
            client.sendMessage(command, this.hardwareControlProperties.getTimeout(), promise);
            log.info("【发送给中控】的消息：{}", command);
            String responseMessage = promise.get();
            client.close();
            return HardwareControlCommandUtil.getReceiveCommandDescription(responseMessage);
        } catch (Exception e) {
            String errMsg = "发送指令到中控系统出错";
            log.error(errMsg, e);
            return null;
        }
    }

//    /**
//     * 通过文字获取中控指令集合
//     * @param message 文字信息
//     * @return 中控指令集合
//     */
//    private List<String> getHardwareCommand(String message) {
//        List<String> command = HardwareControlCommandUtil.parse(message);
//        if (log.isDebugEnabled()) {
//            log.debug("将【{}】通过系统解析后得到的中控指令【{}】", message, command);
//        }
//        return command;
//    }

    /**
     * 通过文字获取中控指令集合
     * @param message 文字信息
     * @return 中控指令集合
     */
    private String getHardwareCommand2(String message) {
        String command = HardwareControlCommandUtil.getCommandValue(message);
        if (log.isDebugEnabled()) {
            log.debug("将【{}】通过系统解析后得到的中控指令【{}】", message, command);
        }
        return command;
    }
}
