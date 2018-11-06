package com.wafersystems.notice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wafersystems.notice.util.DateUtil;
import com.wafersystems.notice.util.HttpClientUtil;
import com.wafersystems.notice.util.HttpsPostClientUtil;
import com.wafersystems.notice.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.NameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NoticeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NoticeApplicationTests {

    @Autowired
    private WebApplicationContext context;

    // 模拟http请求
    private MockMvc mockMvc;

    @Before
    public void init() {
        //集成Web环境测试（此种方式并不会集成真正的web环境，而是通过相应的Mock API进行模拟测试，无须启动服务器）
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * 测试发送邮件接口
     * @throws Exception
     */
    @Test
    public void sendMailTest() throws Exception {
        List<String> params = new ArrayList<String>();
        params.add(DateUtil.formatDateTime("2018-11-06 20:30:00").getTime() + "");// 开始时间
        params.add(DateUtil.formatDateTime("2018-11-06 21:30:00").getTime() + "");// 结束时间
        params.add("-1");// 状态(-1.纯文本信息,0.邮件事件[带邀请按钮],1.邮件事件[无按钮])
        params.add("测试");// 邮件title称呼
        params.add("7");// 邮件类型(1-邀请|2-被创建|3-主持人邀请|4-删除参会人|5-创建人取消会议邮件|6-参会人取消邮件|7-提醒|8-修改|9-同意邀请|10-拒绝邀请)
        params.add("谈东魁");// 创建人或被邀请者
        params.add("华山");// 会议室名称
        params.add("开发会议");// 会议主题
        params.add("李四");// 主持人
        params.add("张三;王五");// 参会人姓名
        params.add("无");// 会议备注
        params.add("每天6:00——7:00");// 会议周期
        params.add("");// (value5=9和value5=10时 回执邮件中 **接受或**拒绝了会议邀请中的 **）
        params.add("https://bkdev.virsical.cn:8499/smartmeeting/smart/third/jumpToWebEx?meetingId=32&type=1");// WebEx会议URL
        params.add("https://bkdev.virsical.cn:8499/smartmeeting/smart/third/jumpToReceipt?meetingId=32&userId=zhangyi&type=0");// 回执URL

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("tempName", "meeting");
        map.set("subject", StringUtil.replaceStr("测试邮件"));
        map.set("toMail", "tandongkui@wafersystems.com");
        map.set("copyTo", "286414791@qq.com");
        map.set("logo", "2");
        map.set("lang", "zh_CN");
        int count = 6;
        for (String temp : params) {
            map.set("value" + (count - 5), StringUtil.replaceStr(temp));
            count++;
        }

        String mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/mail/sendMail").params(map))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        log.info("[/mail/sendMail]发送邮件测试结果：[{}]", mvcResult);
    }

    /**
     * 测试发送短信接口
     * @throws Exception
     */
    @Test
    public void sendSmsTest() throws Exception {
        String telephones = "15591870586";
        String content = "测试信息";
        String lang = "zh_CN";
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        String url = "http://localhost:20110/sms/sendSms";
        telephones = telephones.replaceAll(";", ",");
        telephones = telephones.replaceAll("\\+86", "").replaceAll("\\+852", "");
        if (url.toLowerCase().startsWith("http://")) {
            map.set("url", "http://bill.virsical.cn/caas/caashelper/caasAbility/smsSendByTemplet");
            map.set("token", "E2327CDED0C6968438F1A1A936FE0B6A75121213470985B173B02066A5B0269E2A29598CF11D070BB8728088BFA01AC2");
            map.set("calleeNbr", telephones);
            if (!StringUtil.isEmptyStr(lang) && lang.contains("en")) {
                map.set("templetId", "100428");
            } else {
                map.set("templetId", "100427");
            }
            map.set("clientId", "100002");
            map.set("secret", "wafer");
            map.set("value1", StringUtil.replaceStr(content));
            map.set("requestTime", System.currentTimeMillis() + "");
            map.set("lang", StringUtil.isEmptyStr(lang) ? "zh_CN" : lang);
        }
//          else if (url.toLowerCase().startsWith("https://")) {
//            map.set("url", "http://bill.virsical.cn/caas/caashelper/caasAbility/smsSendByTemplet");
//            map.set("token", "E2327CDED0C6968438F1A1A936FE0B6A75121213470985B173B02066A5B0269E2A29598CF11D070BB8728088BFA01AC2");
//            map.set("calleeNbr", telephones);
//            if (!StringUtil.isEmptyStr(lang) && lang.contains("en")) {
//                map.set("templetId", "100428");
//            } else {
//                map.set("templetId", "100427");
//            }
//            map.set("clientId", "100002");
//            map.set("secret", "wafer");
//            map.set("value1", StringUtil.replaceStr(content));
//            map.set("requestTime", System.currentTimeMillis() + "");
//            map.set("lang", StringUtil.isEmptyStr(lang) ? "zh_CN" : lang);
//        }

        String mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/sms/sendSms").params(map))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        log.info("[/sms/sendSms]发送短信测试结果：[{}]", mvcResult);
    }

}
