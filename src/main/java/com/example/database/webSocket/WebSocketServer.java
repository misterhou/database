package com.example.database.webSocket;

import com.alibaba.fastjson.JSONObject;
import com.example.database.contant.MyContants;
import com.example.database.entity.InterlocutionResult;
import com.example.database.entity.ReturnVo;
import com.example.database.service.InstructionSetService;
import com.example.database.utils.HttpClientUtil;
import com.example.database.utils.ZenzeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    /**
     * 大模型请求参数模板
     */
    private static String largeModelParamTemplateFilePath;

    /**
     * 调用第三系统出错提示信息
     */
    private static String errorMessage = "";

    /**
     * 大模型响应数据存放字段
     */
    private static String largeModelResponseDataField = "";

    @Value("${fan-yu.error-message}")
    public void setErrorMessage(String errorMessage) {
        WebSocketServer.errorMessage = errorMessage;
    }

    @Value("${fan-yu.large-model.param.template-file-path}")
    public void setLargeModelParamTemplateFilePath(String largeModelParamTemplateFilePath) {
        WebSocketServer.largeModelParamTemplateFilePath = largeModelParamTemplateFilePath;
    }
    @Value("${fan-yu.large-model.param.response-data-field}")
    public void setLargeModelResponseDataField(String responseDataField) {
        WebSocketServer.largeModelResponseDataField = responseDataField;
    }

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
    @OnMessage
    public void onMessage(String message) {
        log.info("【接收到天工】的消息:{}", message);
        InterlocutionResult ilr = httpHaveInterlocutionResult(message);
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
            if (StringUtils.isBlank(ilr.getId()) || "无".equals(ilr.getId())) {
                returnVo.setResults(MyContants.NOT_HAVE_ID);
//                InterlocutionResultV2 largeModelResponse = httpHaveInterlocutionResultV2(message);
//                if (largeModelResponse != null) {
//                    String largeModelResponseResult = largeModelResponse.getResult();
                    String largeModelResponseResult = getAnswerFromLargeModel(message);
                    if (StringUtils.isNotBlank(largeModelResponseResult)) {
                        returnVo.setResults(largeModelResponseResult);
                    } else {
                        returnVo.setResults(WebSocketServer.errorMessage);
                    }
//                } else {
//                    returnVo.setResults(WebSocketServer.errorMessage);
//                }
            }else {
                 instructionSetService.haveReturnVo(ilr, returnVo, message);
            }
        } else {
            returnVo.setResults(WebSocketServer.errorMessage);
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

//    @OnMessage
//    public void onMessage(String message) {
//        InterlocutionResultV2 ilr = httpHaveInterlocutionResultV2(message);
//        log.info("返回的数据:{}", ilr);
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
////            if (StringUtils.isBlank(ilr.getId()) || "无".equals(ilr.getId())) {
////                returnVo.setResults(MyContants.NOT_HAVE_ID);
////            }else {
////                 instructionSetService.haveReturnVo(ilr, returnVo);
////            }
//            returnVo.setResults(ilr.getResult());
//
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

    private InterlocutionResult httpHaveInterlocutionResult(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("text", message);
        Class<InterlocutionResult> c = InterlocutionResult.class;
        String url = instructionSetService.getEnv();
        log.info("【发送给 8017】的消息：{}", map);
        InterlocutionResult ilr = HttpClientUtil.postFormForObject(url, map, c);
//        InterlocutionResult ilr = new InterlocutionResult();
//        ilr.setId("2");
//        ilr.setDirective("2023年5月25日");
//        ilr.setDirectiveType("运行指令");
//        ilr.setQuestion("负荷");
//        ilr.setTips("大屏前空调1号大屏前西侧空调打开空调");
        log.info("【接收到 8017】的消息：{}", ilr);
        return ilr;
    }

    /**
     * 发送错误时的处理
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误", error);
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
        log.info("【sessionPool】:{}", userId);
        if (session != null && session.isOpen()) {
            try {
//                log.info("【websocket消息】单点消息:" + message);
                log.info("【发送给天工】 的消息：{}", message);
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

//    private InterlocutionResultV2 httpHaveInterlocutionResultV2(String message) {
////        Map<String, String> map = new HashMap<>();
////        map.put("text", message);
////        String data = "{\"messages\":[{\"role\":\"user\",\"content\":\"" + message + "\"}]}";
//        String data = this.getLargeModelParam(message);
//        log.info("【发给大模型】的消息：{}", data);
//        String url = instructionSetService.getLargeModelUrl();
////        return HttpClientUtil.postFormForObject(url, map, InterlocutionResultV2.class);
//        InterlocutionResultV2 ivr = HttpClientUtil.postJsonForObject(url, data, InterlocutionResultV2.class);
//        log.info("【接收到大模型】的消息：{}", ivr);
//        return ivr;
//    }

    /**
     * 获取答案通过大模型
     *
     * @param message 问题
     * @return 答案
     */
    private String getAnswerFromLargeModel(String message) {
        String answer = null;
        String data = this.getLargeModelParam(message);
        log.info("【发给大模型】的消息：{}", data);
        String url = instructionSetService.getLargeModelUrl();
        try {
            String largeModelResult = HttpClientUtil.sendPostJson(url, data);
            JSONObject largeModelResponse = JSONObject.parseObject(largeModelResult);
            log.info("【接收到大模型】的消息：{}", largeModelResponse);
            answer = Ognl.getValue(WebSocketServer.largeModelResponseDataField, largeModelResponse).toString();
        } catch (Exception e) {
            log.error("接收大模型响应数据出错", e);
        }
        return answer;
    }

    /**
     * 获取大模型请求参数
     *
     * @param message 消息
     * @return 大模型请求参数
     */
    private String getLargeModelParam(String message) {
        String template = this.getLargeModelParamTemplate();
        return template.replace("${message}", message);
    }

    /**
     * 获取大模型请求参数模板
     *
     * @return 大模型请求参数模板
     */
    private String getLargeModelParamTemplate() {
        StringBuilder data = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(WebSocketServer.largeModelParamTemplateFilePath),
                    StandardCharsets.UTF_8);
            int ch = 0;
            while ((ch = inputStreamReader.read()) != -1) {
                data.append((char) ch);
            }
        } catch (IOException e) {
            log.error("读取大模型参数文件出错", e);
        } finally {
            if (null != inputStreamReader) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    log.error("关闭读取大模型参数文件输入流出错", e);
                }
            }
        }
        String dataStr = data.toString();
        log.info("读取到大模型参数文件的数据：{}", dataStr);
        return dataStr;
    }
}
