package com.example.database.fanyumeta.server;

import com.alibaba.fastjson.JSON;
import com.example.database.fanyumeta.server.tellhow.TellHowResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
@ServerEndpoint("/tell-how")
public class TellHowServer {

    private static List<Session> sessionList =  new CopyOnWriteArrayList<>();

    private static String picAddr = "";

    @Value("${fan-yu.tell-how.pic-addr}")
    public void setPicAddr(String picAddr) {
        TellHowServer.picAddr = picAddr;
    }
    @OnOpen
    public void onOpen(Session session) {
        sessionList.add(session);
        log.info("TellHowServer onOpen");
    }

    @OnClose
    public void onClose(Session session) {
        sessionList.remove(session);
        log.info("TellHowServer onClose");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("TellHowServer onMessage: {}", message);
        try {
            session.getBasicRemote().sendText("{\"id\":\"无\",\"jpgPath\":\"无\",\"results\":\"TellHowServer onMessage" + message + "\"," +
                    "\"tabData\":\"无\"}");
        } catch (IOException e) {
            log.error("TellHowServer onMessage error: {}", e.getMessage());
        }
    }

    @OnError
    public void onError(Throwable error) {
        log.info("TellHowServer onError: {}", error.getMessage());
    }

    public static void noticeClient(String picName, Session... client) {
        List<Session> clients = null;
        if (ObjectUtils.isEmpty(client)) {
            clients = sessionList;
        } else {
            clients = Arrays.asList(client);
        }
        for (Session session : clients) {
            try {
                TellHowResponseMessage tellHowResponseMessage = new TellHowResponseMessage("123",
                        TellHowServer.picAddr + picName);
                if (!StringUtils.isEmpty(picName)) {
                    tellHowResponseMessage.setResult(true);
                }
                String jsonObject = JSON.toJSONString(tellHowResponseMessage);
                session.getBasicRemote().sendText(jsonObject);
            } catch (IOException e) {
                log.error("TellHowServer noticeClient error: {}", e.getMessage());
            }
        }
    }
}
