package com.example.database.fanyumeta.server;

import com.alibaba.fastjson.JSON;
import com.example.database.fanyumeta.server.gh.GHRequestMessage;
import com.example.database.fanyumeta.server.gh.GHResponseMessage;
import com.example.database.fanyumeta.server.tellhow.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 广哈服务
 */
@Component
@Slf4j
@ServerEndpoint("/gh")
public class GHServer {

    private static List<Session> sessionList =  new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session) {
        sessionList.add(session);
        log.info("【广哈服务】客户端已连接");
    }

    @OnClose
    public void onClose(Session session) {
        sessionList.remove(session);
        log.info("【广哈服务】客户端已断开");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("【接收到广哈】的消息: {}", message);
        try {
            GHRequestMessage ghRequestMessage = JSON.parseObject(message, GHRequestMessage.class);
            if (null != ghRequestMessage) {
                if (ServiceType.CALL_OUT.equals(ghRequestMessage.getServiceType())) {
                    this.sendMessage2TellHow(ghRequestMessage);
                }
            }
        } catch (Exception e) {
            log.error("【接收到广哈】的消息处理出错", e);
        }
    }

    /**
     * 处理客户端发来的数据流消息
     * @param session websocket 会话
     * @param message 接收到的消息
     */
    @OnMessage
    public void onMessage(Session session, ByteBuffer message) {
        log.info("【接收到广哈】的二进制消息: {}", message.array());
    }

    @OnError
    public void onError(Throwable error) {
        log.info("【广哈服务】异常: {}", error.getMessage());
    }

    /**
     * 发送消息到客户端
     * @param ghResponseMessage 消息
     * @param client 客户端，为空发送给所有客户端
     */
    public static void noticeClient(GHResponseMessage ghResponseMessage, Session... client) {
        List<Session> clients = null;
        if (ObjectUtils.isEmpty(client)) {
            clients = sessionList;
        } else {
            clients = Arrays.asList(client);
        }
        for (Session session : clients) {
            try {
                String jsonObject = JSON.toJSONString(ghResponseMessage);
                session.getBasicRemote().sendText(jsonObject);
                log.info("【发送给广哈】的消息：{}", jsonObject);
            } catch (IOException e) {
                log.error("【发送给广哈】消息出错: {}", e.getMessage());
            }
        }
    }

    private void sendMessage2TellHow(GHRequestMessage ghRequestMessage) {
        if (null != ghRequestMessage && null != ghRequestMessage.getData()) {
            ResponseMessage responseMessage = new ResponseMessage(ghRequestMessage);
            TellHowServer.noticeClient2(responseMessage);
        }
    }
}
