package com.example.database.webSocket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.database.contant.MyContants;
import com.example.database.entity.InterlocutionResult;
import com.example.database.entity.ReturnVo;
import com.example.database.fanyumeta.client.NanRuiClient;
import com.example.database.fanyumeta.server.ServiceType;
import com.example.database.fanyumeta.server.TellHowServer;
import com.example.database.fanyumeta.server.tellhow.PicType;
import com.example.database.fanyumeta.server.tellhow.ResponseMessage;
import com.example.database.fanyumeta.utils.PicDataUtil;
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

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 南瑞客户端
     */
    private static NanRuiClient nanRuiClient;

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

    /**
     * 大模型响应数据出处存放字段
     */
    private static String largeModelResponseDataSourceField = "";

    /**
     * 大模型响应数据出处前缀
     */
    private static String largeModelSourcePrefix = "";

    /**
     * 大模型响应数据出处后缀
     */
    private static String largeModelSourceSuffix = "";

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

    @Value("${fan-yu.large-model.param.response-data-source-field}")
    public void setLargeModelResponseDataSourceField(String responseDataSourceField) {
        WebSocketServer.largeModelResponseDataSourceField = responseDataSourceField;
    }

    @Value("${fan-yu.large-model.source.prefix}")
    public void setLargeModelSourcePrefix(String sourcePrefix) {
        WebSocketServer.largeModelSourcePrefix = sourcePrefix;
    }

    @Value("${fan-yu.large-model.source.suffix}")
    public void setLargeModelSourceSuffix(String sourceSuffix) {
        WebSocketServer.largeModelSourceSuffix = sourceSuffix;
    }

    @Autowired
    public void setInstructionSetService(InstructionSetService instructionSetService) {
        WebSocketServer.instructionSetService = instructionSetService;
    }

    @Resource
    public void setNanRuiClient(NanRuiClient nanRuiClient) {
        WebSocketServer.nanRuiClient = nanRuiClient;
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
        Pattern weatherPattern = Pattern.compile("天气.*(怎么样|如何)");
        Pattern closePicPattern = Pattern.compile("^关闭.*图$");
        Pattern openPicPattern = Pattern.compile("^打开.*图$");
        Pattern openWiringPicPattern = Pattern.compile("^打开.*接线图$");
        Pattern openIntervalPicPattern = Pattern.compile("^打开.*间隔图$");
        Pattern openContactPicPattern = Pattern.compile("^打开.*联络图$");
        Pattern openSourcePicPattern = Pattern.compile("^打开.*溯源图$");
        Pattern operationPattern = Pattern.compile("^介绍.*运行情况$");
        if (weatherPattern.matcher(message).find()) {   // 天气
            String cityName = message.substring(0, message.indexOf("天气"));
            if (cityName.contains("今天") || cityName.contains("今日")) {
                cityName = cityName.replaceAll("(今天|今日)", "");
            }
            String weatherInfo = WebSocketServer.nanRuiClient.getWeather(cityName);
            if (StringUtils.isNotBlank(weatherInfo)) {
                returnVo.setResults(weatherInfo);
            }
        } else if (closePicPattern.matcher(message).find()) {   // 关图
            returnVo.setResults(MyContants.YX_ZL_ANS);
            ResponseMessage responseMessage = new ResponseMessage(null,
                    ServiceType.CLOSE_NOTICE, null, null);
            TellHowServer.noticeClient2(responseMessage);
        } else if (openPicPattern.matcher(message).find()) { // 开图
            if (openContactPicPattern.matcher(message).find()) { // 打开联络图
                String substationRtKeyId = PicDataUtil.getSubstationRtKeyId(message);
                if (StringUtils.isNotBlank(substationRtKeyId)) {
                    returnVo.setResults(this.getOpenPicNotice(message));
                    // 通知数智人往右挥手
                    returnVo.setPoseId("3");
                    TellHowServer.noticeClient(substationRtKeyId, PicType.CONTACT, null);
                }
            } else if (openIntervalPicPattern.matcher(message).find()) { // 打开间隔图
                String picName = PicDataUtil.getIntervalPicName(message);
                if (StringUtils.isNotBlank(picName)) {
                    returnVo.setResults(this.getOpenPicNotice(message));
                    // 通知数智人往右挥手
                    returnVo.setPoseId("3");
                    TellHowServer.noticeClient(picName, null);
                } else {
                    log.warn("【开间隔图指令】没有获取到对应的间隔图片数据，开图指令：{}，对应的间隔图片名称：{}", message, picName);
                }
            } else if (openSourcePicPattern.matcher(message).find()) { // 打开溯源图
                String substationRtKeyId = PicDataUtil.getSourcePicRtKeyId(message);
                if (StringUtils.isNotBlank(substationRtKeyId)) {
                    returnVo.setResults(this.getOpenPicNotice(message));
                    // 通知数智人往右挥手
                    returnVo.setPoseId("3");
                    TellHowServer.noticeClient(substationRtKeyId, PicType.SOURCE, null);
                }
            } else {
                String picName = PicDataUtil.getPicName(message);
                if (StringUtils.isNotBlank(picName)) {
                    returnVo.setResults(this.getOpenPicNotice(message));
                    // 通知数智人往右挥手
                    returnVo.setPoseId("3");
                    TellHowServer.noticeClient(picName, null);
                } else {
                    log.warn("【开图指令】没有获取到对应的图片数据，开图指令：{}，对应的图片名称：{}", message, picName);
                }
                if (openWiringPicPattern.matcher(message).find()) { // 打开接线图
                    log.info("打开接线图：{}", message);
                } else {
                    log.info("打开图：{}", message);
                }
            }
        } else if (operationPattern.matcher(message).find()) {  // 介绍XX运行情况
            String deviceName = message.replace("介绍", "").replace("运行情况", "");
            deviceName = deviceName.replace("的", "");
            String unitAgcInfo = nanRuiClient.getUnitAgc(deviceName);
            if (StringUtils.isNotBlank(unitAgcInfo)) {
                returnVo.setResults(unitAgcInfo);
            } else {
                returnVo.setResults(MyContants.RESULT_FAIL2);
            }
        } else {
            InterlocutionResult ilr = httpHaveInterlocutionResult(message);
            if (ilr != null){
                String commandId = ilr.getId();
                if (StringUtils.isBlank(commandId) || "无".equals(commandId)) {
                    returnVo.setResults(MyContants.NOT_HAVE_ID);
                    String largeModelResponseResult = getAnswerFromLargeModel(message);
                    if (StringUtils.isNotBlank(largeModelResponseResult)) {
                        returnVo.setResults(largeModelResponseResult);
                    } else {
                        returnVo.setResults(WebSocketServer.errorMessage);
                    }
                } else {
                    // 开图指令
//                    if (MyContants.KAI_TU.equals(ilr.getDirectiveType())) {
//                        String picName = PicDataUtil.getPicName(commandId);
//                        if (StringUtils.isNotBlank(picName)) {
//                            returnVo.setResults(this.getOpenPicNotice(message));
//                            // 通知数智人往右挥手
//                            returnVo.setPoseId("3");
//                            TellHowServer.noticeClient(picName, null);
//                        } else {
//                            log.warn("【开图指令】没有获取到对应的图片数据，开图指令：{}，对应的图片名称：{}", message, picName);
//                        }
//                    } else {
                        instructionSetService.haveReturnVo(ilr, returnVo, message);
//                    }
                }
            } else {
                returnVo.setResults(WebSocketServer.errorMessage);
            }
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
            Object fieldValue = Ognl.getValue(WebSocketServer.largeModelResponseDataField, largeModelResponse);
            if (fieldValue instanceof String) {
                answer = fieldValue.toString().replaceAll("\n", "");
                String docName = getDocName(largeModelResponse);
                if (StringUtils.isNotBlank(docName)) {
                    String largeModelSource =
                            WebSocketServer.largeModelSourcePrefix + docName + WebSocketServer.largeModelSourceSuffix;
                    answer = largeModelSource + answer;
                }
            }
            log.info("【解析大模型】响应数据，解析参数：{}，解析结果：{}", WebSocketServer.largeModelResponseDataField, answer);
        } catch (Exception e) {
            log.error("【解析大模型】响应数据出错，解析参数：{}", WebSocketServer.largeModelResponseDataField, e);
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

    /**
     * 获取大模型数据出处
     * @param largeModelResponse 大模型返回数据
     * @return 数据出处
     */
    private String getDocName(JSONObject largeModelResponse) {
        JSONArray docs = null;
        try {
            Object fieldValue = Ognl.getValue(WebSocketServer.largeModelResponseDataSourceField, largeModelResponse);
            if (fieldValue instanceof JSONArray) {
                docs = (JSONArray) fieldValue;
            }
        } catch (OgnlException e) {
            throw new RuntimeException(e);
        }
        HashSet<String> docNames = new HashSet<>();
        if (docs != null && docs.size() > 0) {
            for (Object doc : docs) {
                if (doc instanceof String) {
                    String docContent = (String) doc;
                    Pattern pattern = Pattern.compile("\\[(.*?)\\]");
                    Matcher matcher = pattern.matcher(docContent);
                    List<String> targetContent = new ArrayList<>();
                    while (matcher.find()) {
                        targetContent.add(matcher.group());
                    }
                    if (targetContent.size() >= 2) {
                        String docName = targetContent.get(1);
                        docNames.add(docName.substring(1, docName.length() - 1).replaceAll("\\.[^\\.]+$", ""));
                    }
                }
            }
        }
        return String.join("，", docNames);
    }

    /**
     * 获取打开图片的提示语
     *
     * @param message 消息
     * @return 开图提示语
     */
    private String getOpenPicNotice(String message) {
        int index = message.indexOf("打开");
        return "好的，已打开" + message.substring(index + 2) + "，请查看";
    }
}
