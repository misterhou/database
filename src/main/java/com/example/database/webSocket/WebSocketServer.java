package com.example.database.webSocket;

import com.example.database.contant.MyContants;
import com.example.database.entity.InterlocutionResult;
import com.example.database.entity.InterlocutionResultV2;
import com.example.database.entity.ReturnVo;
import com.example.database.service.InstructionSetService;
import com.example.database.utils.HttpClientUtil;
import com.example.database.utils.ZenzeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket操作类
 *
 * @author zxx
 * @date 2022-08-22 10:00
 * @since 1.0.0
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {

//    // 先定义，autowired会在类加载后自动注入（解决@Component类中@Service等注解注入失败的情况）
//    private static RoadshowQAService roadshowQAService;

    // 当前用户id
    private String userId;
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //新：使用map对象优化，便于根据sid来获取对应的WebSocket
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    private static ConcurrentHashMap<String, WebSocketServer> websocketMap = new ConcurrentHashMap<>();

    // 用来存在线连接数
    private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

    private static InstructionSetService instructionSetService;

    @Autowired
    public void setInstructionSetService(InstructionSetService instructionSetService) {
        WebSocketServer.instructionSetService = instructionSetService;
    }

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        try {
            this.session = session;
            this.userId = userId;
            websocketMap.put(userId, this);
            sessionPool.put(userId, session);
            System.out.println(userId);
            System.out.println(session);
            log.info("【websocket消息】有新的连接，总数为:" + websocketMap.size());
            sendAllMessage("成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            if (websocketMap.containsKey(this.userId)) {
                websocketMap.remove(this.userId);
                sessionPool.remove(this.userId);
            }
            log.info("【websocket消息】连接断开，总数为:" + websocketMap.size());
//            System.out.println("杀死线程"+this.userId);
//            boolean tag=KillThreadByName.kill(this.userId);
//            if(tag){
//                System.out.println("成功");
//            }else {
//                System.out.println("失败");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     */
//    @OnMessage
//    public void onMessage(String message) {
//        InterlocutionResult ilr = httpHaveInterlocutionResult(message);
//        //log.info("返回的数据:{}" + ilr);
//        Integer sjInt = ZenzeUtils.haveSj();
//        ReturnVo returnVo = new ReturnVo();
//        returnVo.setId("无");
//        returnVo.setJpgPath("无");
//        if (sjInt%2 == 0) {
//            returnVo.setResults(MyContants.RESULT_FAIL1);
//        } else {
//            returnVo.setResults(MyContants.RESULT_FAIL2);
//        }
//        returnVo.setTabData("无");
//        if (ilr != null){
//            if (StringUtils.isBlank(ilr.getId()) || "无".equals(ilr.getId())) {
//                returnVo.setResults(MyContants.NOT_HAVE_ID);
//            }else {
//                 instructionSetService.haveReturnVo(ilr, returnVo);
//            }
//        }
//
//        //创建 ObjectMapper 对象
//        ObjectMapper objectMapper = new ObjectMapper();
//        // 将对象转换为 JSON 字符串
//        String jsonString = null;
//        try {
//            jsonString = objectMapper.writeValueAsString(returnVo);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        sendOneMessage(userId, jsonString);
//    }

    @OnMessage
    public void onMessage(String message) {
        InterlocutionResultV2 ilr = httpHaveInterlocutionResultV2(message);
        log.info("返回的数据:{}", ilr);
        Integer sjInt = ZenzeUtils.haveSj();
        ReturnVo returnVo = new ReturnVo();
        returnVo.setId("无");
        returnVo.setJpgPath("无");
        if (sjInt%2 == 0) {
            returnVo.setResults(MyContants.RESULT_FAIL1);
        } else {
            returnVo.setResults(MyContants.RESULT_FAIL2);
        }
        returnVo.setTabData("无");
        if (ilr != null){
//            if (StringUtils.isBlank(ilr.getId()) || "无".equals(ilr.getId())) {
//                returnVo.setResults(MyContants.NOT_HAVE_ID);
//            }else {
//                 instructionSetService.haveReturnVo(ilr, returnVo);
//            }
            returnVo.setResults(ilr.getResult());

        }

        //创建 ObjectMapper 对象
        ObjectMapper objectMapper = new ObjectMapper();
        // 将对象转换为 JSON 字符串
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(returnVo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        sendOneMessage(userId, jsonString);
    }

    private InterlocutionResult httpHaveInterlocutionResult(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("text", message);
        Class<InterlocutionResult> c = InterlocutionResult.class;
        String url = instructionSetService.getEnv();
        return HttpClientUtil.postFormForObject(url, map, c);
    }

    /**
     * 发送错误时的处理
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误,原因:" + error.getMessage());
        error.printStackTrace();
    }


    // 此为广播消息
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:" + message);

        for (Map.Entry<String, WebSocketServer> map : websocketMap.entrySet()) {
            WebSocketServer webSocketServer = map.getValue();
            if (webSocketServer.session.isOpen()) {
                synchronized (session) {
                    webSocketServer.session.getAsyncRemote().sendText(message);
                }
            }
        }
    }

    // 此为单点消息
    public void sendOneMessage(String userId, String message) {
        Session session = sessionPool.get(userId);
        log.info("【sessionPool】:{}" + userId);
        if (session != null && session.isOpen()) {
            try {
                log.info("【websocket消息】单点消息:" + message);
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public void sendOneObject(String userId, InterlocutionResult result) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendObject(result);
                System.out.println(result.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息(多人)
    public void sendMoreMessage(String[] userIds, String message) {
        for (String userId : userIds) {
            Session session = sessionPool.get(userId);
            if (session != null && session.isOpen()) {
                try {
                    log.info("【websocket消息】 单点消息:" + message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private InterlocutionResultV2 httpHaveInterlocutionResultV2(String message) {
//        Map<String, String> map = new HashMap<>();
//        map.put("text", message);
        log.info("接收到调用端穿过来的数据：{}", message);
        String data = "{\"messages\":[{\"role\":\"user\",\"content\":\"" + message + "\"}]}";
        log.info("发给模型的数据：{}", data);
        String url = instructionSetService.getEnv();
//        return HttpClientUtil.postFormForObject(url, map, InterlocutionResultV2.class);
        return HttpClientUtil.postJsonForObject(url, data, InterlocutionResultV2.class);
    }
}
