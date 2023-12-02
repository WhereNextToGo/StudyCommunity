package com.cs_liudi.community.controller;

import com.cs_liudi.community.entity.Message;
import com.cs_liudi.community.entity.Page;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.service.MessageService;
import com.cs_liudi.community.service.UserService;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        List<Message> conversations = messageService.
                findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversationList= new ArrayList<>();
        if(conversations != null){
            for (Message conversation:conversations) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("conversation",conversation);
                map.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));
                map.put("letterUnreadCount",messageService.findUnreadLetterCount(user.getId(),conversation.getConversationId()));
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId(): conversation.getFromId();
                map.put("targetUser",userService.findUserById(targetId));
                conversationList.add(map);
            }
            model.addAttribute("conversations",conversationList);
            model.addAttribute("totalLetterUnreadCount",messageService.findUnreadLetterCount(user.getId(),null));
        }
        return "site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Model model,Page page){
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterlist = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if (letterlist != null){
            for (Message message:letterlist){
                HashMap<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("FromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
            model.addAttribute("letters",letters);
            User targetUser = getTargetUser(conversationId);
            model.addAttribute("targetUser",targetUser);
            List<Integer> ids = getUnreadLetterIdList(letterlist);
            if (!ids.isEmpty()){
                messageService.readMessage(ids);
            }
        }
        return "site/letter-detail";
    }
    public User getTargetUser(String conversationId){
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        User user = hostHolder.getUser();
        if (user.getId() == id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }

    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName,String content){
        User toUser = userService.findUserByName(toName);
        if (toUser == null){
            return CommunityUtils.getJSONString(1,"用户不存在！");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(toUser.getId());
        String conversationId;
        if (message.getFromId() < message.getToId()){
            conversationId = message.getFromId()+"_"+message.getToId();
        }else{
            conversationId = message.getToId()+"_"+message.getFromId();
        }
        message.setConversationId(conversationId);
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtils.getJSONString(0);
    }


    public List<Integer> getUnreadLetterIdList(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for(Message message:letterList){
                if(hostHolder.getUser().getId() == message.getToId()&&message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
}
