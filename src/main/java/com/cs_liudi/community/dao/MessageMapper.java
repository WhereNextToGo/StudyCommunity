package com.cs_liudi.community.dao;

import com.cs_liudi.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface MessageMapper {
    //显示当前用户的会话列表，并且每条会话只显示最新的一条私信
    List<Message> selectConversations(int userId,int offset,int limit);
    //显示用户当前的会话总数
    int selectConversationCount(int userId);
    //显示当前会话的私信详情
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //显示用户当前的会话的私信总数
    int selectLetterCount(String conversationId);
    //显示未读私信数量,分为用户所有未读私信的数量/某一个会话的私信数量
    int selectUnreadLetterCount(int userId, String conversationId);
    //新增私信消息
    int insertMessage(Message message);
    //未读消息变为已读
    int updateStatus(List<Integer> ids,int status);
}
