package com.example.database.fanyumeta.transfer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.database.fanyumeta.transfer.service.ServiceType;
import com.example.database.fanyumeta.transfer.service.TransferService;
import com.example.database.fanyumeta.transfer.vo.ThResponseBody;
import com.example.database.fanyumeta.transfer.vo.TransferRequestBody;
import com.example.database.fanyumeta.transfer.vo.TransferResponseBody;
import com.example.database.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 中转服务实现
 */
@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    /**
     * 广哈服务地址
     */
    private String ghServiceUrl = "http://192.168.1.23:7879/imitate/gh/call/";

    private String ghBroadcastServiceUrl = "http://192.168.1.23:7879/imitate/gh/broadcast/";

    /**
     * 泰豪服务地址
     */
    private String thServiceUrl = "http://192.168.1.23:7879/imitate/th/";

    @Override
    public TransferResponseBody sendMsg2Gh(TransferRequestBody transferRequestBody) {
        String serviceUrl = this.ghServiceUrl;
        ThResponseBody thResponseBody = new ThResponseBody();
        String service = transferRequestBody.getService();
        String url = null;
        if (ServiceType.startcall.name().equals(service)) {
            url = serviceUrl + ServiceType.startcall;
        } else if (ServiceType.answercall.name().equals(service)) {
            url = serviceUrl + ServiceType.answercall;
        } else if (ServiceType.callcontrol.name().equals(service)) {
            url = serviceUrl + ServiceType.callcontrol;
        } else if (ServiceType.releasecall.name().equals(service)) {
            url = serviceUrl + ServiceType.releasecall;
        } else if (ServiceType.textmsg.name().equals(service)) {
            url = serviceUrl + ServiceType.textmsg;
        } else if (ServiceType.startbc.name().equals(service)) {
            url = this.ghBroadcastServiceUrl + ServiceType.startbc;
        } else if (ServiceType.addmember.name().equals(service)) {
            url = this.ghBroadcastServiceUrl + ServiceType.addmember;
        } else {
            thResponseBody.setMessage("不支持的服务类型：" + service);
        }
        if (null != url) {
            Object data = transferRequestBody.getData();
            if (null != data) {
                String paramsStr = JSON.toJSONString(data);
                log.info("请求广哈服务：请求地址：{}, 请求参数：{}", url, paramsStr);
                String response = HttpClientUtil.sendPostJson(url, paramsStr);
                log.info("接收到广哈服务的结果：{}", response);
                JSONObject result = JSONObject.parseObject(response);
                if (null != result) {
                    thResponseBody.setService(service);
                    thResponseBody.setResult(true);
                    thResponseBody.setData(result);
                } else {
                    thResponseBody.setMessage("广哈服务异常");
                }
            } else {
                thResponseBody.setMessage("data 参数为空");
            }
        }
        return thResponseBody;
    }

    @Override
    public TransferResponseBody sendMsg2Th(TransferRequestBody transferRequestBody) {
        TransferResponseBody transferResponseBody = new TransferResponseBody();
        String service = transferRequestBody.getService();
        String url = null;
        if (ServiceType.callevent.name().equals(service)) {
            url = this.thServiceUrl + ServiceType.callevent;
        } else if (ServiceType.dtmfevent.name().equals(service)) {
            url = this.thServiceUrl + ServiceType.callevent;
        } else if (ServiceType.sendtext.name().equals(service)) {
            url = this.thServiceUrl + ServiceType.sendtext;
        } else if (ServiceType.recordaddr.name().equals(service)) {
            url = this.thServiceUrl + ServiceType.recordaddr;
        } else if (ServiceType.memberstatus.name().equals(service)) {
            url = this.thServiceUrl + ServiceType.memberstatus;
        } else if (ServiceType.bcstatus.name().equals(service)) {
            url = this.thServiceUrl + ServiceType.bcstatus;
        } else {
            transferResponseBody.setMessage("不支持的服务类型：" + service);
        }
        if (null != url) {
            Object data = transferRequestBody.getData();
            if (null != data) {
                String paramsStr = JSON.toJSONString(data);
                log.info("请求泰豪服务：请求地址：{}, 请求参数：{}", url, paramsStr);
                String response = HttpClientUtil.sendPostJson(url, paramsStr);
                log.info("接收到泰豪服务的结果：{}", response);
                JSONObject result = JSONObject.parseObject(response);
                if (null != result) {
                    transferResponseBody.setService(service);
                    transferResponseBody.setResult(true);
                    transferResponseBody.setData(result);
                } else {
                    transferResponseBody.setMessage("泰豪服务异常");
                }
            } else {
                transferResponseBody.setMessage("data 参数为空");
            }
        }
        return transferResponseBody;
    }
}
