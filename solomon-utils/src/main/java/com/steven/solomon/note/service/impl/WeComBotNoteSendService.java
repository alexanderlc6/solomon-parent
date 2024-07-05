package com.steven.solomon.note.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.steven.solomon.note.entity.NoteRequestVO;
import com.steven.solomon.note.entity.SendBaseNoteMessage;
import com.steven.solomon.note.profile.DingTalkProfile;
import com.steven.solomon.note.service.BotNoteSendService;
import com.steven.solomon.verification.ValidateUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableConfigurationProperties(value={DingTalkProfile.class})
public class WeComBotNoteSendService implements BotNoteSendService {

    private final DingTalkProfile profile;

    public WeComBotNoteSendService(DingTalkProfile profile) {
        this.profile = profile;
    }

    @Override
    public String initProfile() throws Exception {
        return profile.getUrl();
    }

    @Override
    public NoteRequestVO sendNote(SendBaseNoteMessage message) throws Exception {

        SendBaseNoteMessage.SendLinkMessage linkMessage = message.getLink();
        SendBaseNoteMessage.SendAtMessage sendAtMessage = message.getAt();
        SendBaseNoteMessage.SendTextMessage textMessage = message.getText();
        SendBaseNoteMessage.SendMarkdownMessage markdownMessage= message.getMarkdown();

        Map<String, Object> map = new HashMap<>();
        if(ValidateUtils.isNotEmpty(textMessage)){
            map.put("msgtype", "text");
            Map<String,Object> detailMap = new HashMap<>();
            detailMap.put("content", textMessage.getMessage());
            if(ValidateUtils.isNotEmpty(sendAtMessage)){
                List<String> metionedList = ValidateUtils.getOrDefault(sendAtMessage.getAtUserIds(),new ArrayList<>());
                List<String> metionedMobileList = ValidateUtils.getOrDefault(sendAtMessage.getAtMobiles(),new ArrayList<>());
                if(sendAtMessage.getAtAll()){
                    metionedList.add("@all");
                }
                if(ValidateUtils.isNotEmpty(metionedList)){
                    detailMap.put("mentioned_list", metionedList);
                }
                if(ValidateUtils.isNotEmpty(metionedMobileList)){
                    detailMap.put("mentioned_mobile_list", metionedMobileList);
                }
            }
            map.put("text", detailMap);
        }

        if(ValidateUtils.isNotEmpty(linkMessage)){
            map.put("msgtype", "news");

            Map<String,Object> detailMap = new HashMap<>();
            Map<String,Object> articlesMap = new HashMap<>();
            articlesMap.put("title", linkMessage.getTitle());
            articlesMap.put("description", linkMessage.getText());
            articlesMap.put("url", linkMessage.getMessageUrl());
            articlesMap.put("picurl", linkMessage.getPicUrl());
            detailMap.put("articles",Arrays.asList(articlesMap));
            map.put("news", detailMap);
        }

        if(ValidateUtils.isNotEmpty(markdownMessage)){
            map.put("msgtype", "markdown");

            Map<String,Object> detailMap = new HashMap<>();
            detailMap.put("content", markdownMessage.getTitle());
            map.put("markdown", detailMap);
        }

        HttpResponse response = HttpRequest.post(initProfile()).body(JSONUtil.toJsonStr(map)).execute();
        return new NoteRequestVO(response.isOk(),response.body());
    }

    public static void main(String[] args) throws Exception {
        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=7612021e-1e83-496c-a2f3-beaf99622e24";
        DingTalkProfile dingTalkProfile = new DingTalkProfile();
        dingTalkProfile.setUrl(url);
        WeComBotNoteSendService a = new WeComBotNoteSendService(dingTalkProfile);

        SendBaseNoteMessage message = new SendBaseNoteMessage(new SendBaseNoteMessage.SendTextMessage("a"),new SendBaseNoteMessage.SendAtMessage(null,null,null,true));
        a.sendNote(message);
    }
}
