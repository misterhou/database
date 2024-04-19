package com.example.database.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class MyWebSocketClient extends WebSocketClient {
    public String text;

    // appid
    private final String APPID = "5febed45";

    // appid对应的secret_key
    private final String SECRET_KEY = "a61ff9579e78926146465f9b48be93c2";

    // 请求地址
    private final String HOST = "rtasr.xfyun.cn/v1/ws";

    private final String BASE_URL = "wss://" + HOST;

    private final String ORIGIN = "https://" + HOST;

    // 音频文件路径
//    private static final String AUDIO_PATH = "./resource/test_1.pcm";
    private final String AUDIO_PATH = "E:/output.wav";

    // 每次发送的数据大小 1280 字节
    private final int CHUNCKED_SIZE = 1280;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.SSS");

    private CountDownLatch handshakeSuccess;
    private CountDownLatch connectClose;

    public MyWebSocketClient(URI serverUri, Draft protocolDraft, CountDownLatch handshakeSuccess, CountDownLatch connectClose) {
        super(serverUri, protocolDraft);
        this.handshakeSuccess = handshakeSuccess;
        this.connectClose = connectClose;
        if(serverUri.toString().contains("wss")){
            trustAllHosts(this);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println(getCurrentTimeStr() + "\t连接建立成功！");
    }

    @Override
    public void onMessage(String msg) {
        JSONObject msgObj = JSON.parseObject(msg);
        String action = msgObj.getString("action");
        if (Objects.equals("started", action)) {
            // 握手成功
            System.out.println(getCurrentTimeStr() + "\t握手成功！sid: " + msgObj.getString("sid"));
            handshakeSuccess.countDown();
        } else if (Objects.equals("result", action)) {
            // 转写结果
            System.out.println(getCurrentTimeStr() + "\tresult: " + getContent(msgObj.getString("data")));
            text=getCurrentTimeStr() + "\tresult: " + getContent(msgObj.getString("data"));
        } else if (Objects.equals("error", action)) {
            // 连接发生错误
            System.out.println("Error: " + msg);
            text="Error: " + msg;
            System.exit(0);
        }
    }

    @Override
    public void onError(Exception e) {
        System.out.println(getCurrentTimeStr() + "\t连接发生错误：" + e.getMessage() + ", " + new Date());
        e.printStackTrace();
        System.exit(0);
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        System.out.println(getCurrentTimeStr() + "\t链接关闭");
        connectClose.countDown();
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            System.out.println(getCurrentTimeStr() + "\t服务端返回：" + new String(bytes.array(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void trustAllHosts(MyWebSocketClient appClient) {
        System.out.println("wss");
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setSocket(sc.getSocketFactory().createSocket());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 把转写结果解析为句子
    public String getContent(String message) {
        StringBuffer resultBuilder = new StringBuffer();
        try {
            JSONObject messageObj = JSON.parseObject(message);
            JSONObject cn = messageObj.getJSONObject("cn");
            JSONObject st = cn.getJSONObject("st");
            JSONArray rtArr = st.getJSONArray("rt");
            for (int i = 0; i < rtArr.size(); i++) {
                JSONObject rtArrObj = rtArr.getJSONObject(i);
                JSONArray wsArr = rtArrObj.getJSONArray("ws");
                for (int j = 0; j < wsArr.size(); j++) {
                    JSONObject wsArrObj = wsArr.getJSONObject(j);
                    JSONArray cwArr = wsArrObj.getJSONArray("cw");
                    for (int k = 0; k < cwArr.size(); k++) {
                        JSONObject cwArrObj = cwArr.getJSONObject(k);
                        String wStr = cwArrObj.getString("w");
                        resultBuilder.append(wStr);
                    }
                }
            }
        } catch (Exception e) {
            return message;
        }

        return resultBuilder.toString();
    }

    // 生成握手参数
    public String getHandShakeParams(String appId, String secretKey) {
        String ts = System.currentTimeMillis()/1000 + "";
        String signa = "";
        try {
            signa = EncryptUtil.HmacSHA1Encrypt(EncryptUtil.MD5(appId + ts), secretKey);
            return "?appid=" + appId + "&ts=" + ts + "&signa=" + URLEncoder.encode(signa, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    public String getCurrentTimeStr() {
        return sdf.format(new Date());
    }
}
