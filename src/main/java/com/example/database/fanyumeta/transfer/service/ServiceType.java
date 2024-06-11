package com.example.database.fanyumeta.transfer.service;

/**
 * 服务类型
 */
public enum ServiceType {

    /**
     * 发起呼叫
     */
    startcall,

    /**
     * 接听通话
     */
    answercall,

    /**
     * 关闭呼叫
     */
    releasecall,

    /**
     * 呼叫控制
     */
    callcontrol,

    /**
     * 通话状态上报
     */
    callevent,

    /**
     * 按键事件（dtmf）上报
     */
    dtmfevent,

    /**
     * 通知文本下发
     */
    textmsg,

    /**
     * 翻译文本上报
     */
    sendtext,

    /**
     * 呼叫录音链接上报
     */
    recordaddr,

    /**
     * 发起广播
     */
    startbc,

    /**
     * 增加呼叫成员
     */
    addmember,

    /**
     * 广播成员状态上报
     */
    memberstatus,

    /**
     * 广播结束状态上报
     */
    bcstatus;

}
