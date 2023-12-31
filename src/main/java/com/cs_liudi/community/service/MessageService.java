package com.cs_liudi.community.service;

import com.cs_liudi.community.dao.MessageMapper;
import com.cs_liudi.community.entity.Message;
import com.cs_liudi.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }
    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }
    public int findUnreadLetterCount(int userId, String conversationId){
        return messageMapper.selectUnreadLetterCount(userId,conversationId);
    }
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids, 1);
    }

}
