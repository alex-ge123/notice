package com.wafersystems.notice.model;

import lombok.Data;

@Data
public class SmsRecordVO {
    private String callee;

    private String domain;

    private String sessionid;

    private String sign;

    private short status;

    private String userid;


    /**
     * sms 、voice、inter_sms
     */
    private String servicetype;
    /**
     * 实际短信数量
     */
    private int smsNum;

    //短信余额
    private long smsBalance = 0;
    //短信总量
    private long smsNumber = 0;
    //国际短信余额
    private long interSmsBalance = 0;
    //国际短信总量
    private long interSmsNumber = 0;
    //语音通知余额
    private long voiceBalance = 0;
    //语音通知总量
    private long voiceNumber = 0;
    //国际语音通知余额
    private long interVoiceBalance = 0;
    //国际语音通知总量
    private long interVoiceNumber = 0;
}
