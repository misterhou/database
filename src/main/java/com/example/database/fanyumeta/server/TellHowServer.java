package com.example.database.fanyumeta.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.database.fanyumeta.server.gh.GHResponseMessage;
import com.example.database.fanyumeta.server.tellhow.ResponseMessage;
import com.example.database.fanyumeta.server.tellhow.TellHowRequestMessage;
import com.example.database.fanyumeta.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        log.info("【泰豪服务】客户端已连接");
    }

    @OnClose
    public void onClose(Session session) {
        sessionList.remove(session);
        log.info("【泰豪服务】客户端已断开");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("【接收到泰豪】的消息: {}", message);
        TellHowRequestMessage tellHowRequestMessage = null;
        try {
            tellHowRequestMessage = JSONObject.parseObject(message, TellHowRequestMessage.class);
            if (null != tellHowRequestMessage) {
                if (Service.CALL_OUT.equals(tellHowRequestMessage.getService())) {
                    this.sendMessage2Gh(tellHowRequestMessage);
                }
            }
        } catch (Exception e) {
            log.error("【接收到泰豪】的消息处理出错", e);
        }
//
//        try {
//
//            session.getBasicRemote().sendText("{\"id\":\"无\",\"jpgPath\":\"无\",\"results\":\"TellHowServer onMessage\"," +
//                    "\"tabData\":\"无\"}");
//        } catch (IOException e) {
//            log.error("TellHowServer onMessage error: {}", e.getMessage());
//        }
    }

    @OnError
    public void onError(Throwable error) {
        log.info("【泰豪服务】异常: {}", error);
    }

    /**
     * 发送给客户端开图消息
     * @param picName 图片名称
     * @param client 客户端，为空发给所有客户端
     */
    public static void noticeClient(String picName, Session... client) {
        List<Session> clients = null;
        if (ObjectUtils.isEmpty(client)) {
            clients = sessionList;
        } else {
            clients = Arrays.asList(client);
        }
        for (Session session : clients) {
            try {
//                TellHowResponseMessage data = new TellHowResponseMessage("123",
//                        TellHowServer.picAddr + picName);
//                if (!StringUtils.isEmpty(picName)) {
//                    data.setResult(true);
//                }
                if (StringUtils.hasText(picName)) {
                    Map<String, String> data = new HashMap<>();
                    data.put("picUrl", TellHowServer.picAddr + picName);
                    ResponseMessage tellHowResponseMessage = new ResponseMessage(null, Service.KAI_TU, data);
                    String jsonObject = JSON.toJSONString(tellHowResponseMessage);
                    session.getBasicRemote().sendText(jsonObject);
                    log.info("【发送给泰豪】的开图消息：{}", jsonObject);
                } else {
                    log.warn("开图图片为空");
                }
            } catch (IOException e) {
                log.error("TellHowServer noticeClient error: {}", e.getMessage());
            }
        }
    }

    /**
     * 发送给泰豪的消息
     * @param message 图片名称
     * @param client 客户端，为空发给所有客户端
     */
    public static void noticeClient2(ResponseMessage message, Session... client) {
        List<Session> clients = null;
        if (ObjectUtils.isEmpty(client)) {
            clients = sessionList;
        } else {
            clients = Arrays.asList(client);
        }
        for (Session session : clients) {
            try {
                String jsonObject = JSON.toJSONString(message);
                session.getBasicRemote().sendText(jsonObject);
                log.info("【发送给泰豪】的消息：{}", jsonObject);
            } catch (IOException e) {
                log.error("【发送给泰豪】的消息出错", e);
            }
        }
    }

    /**
     * 发送消息到广哈
     * @param tellHowRequestMessage 消息
     */
    private void sendMessage2Gh(TellHowRequestMessage tellHowRequestMessage) {
        if (null != tellHowRequestMessage) {
            try {
                GHResponseMessage ghResponseMessage = new GHResponseMessage(tellHowRequestMessage);
                GHServer.noticeClient(ghResponseMessage);
            } catch (Exception e) {
                log.error("发送消息到广哈出错", e);
            }
        }
    }
}
