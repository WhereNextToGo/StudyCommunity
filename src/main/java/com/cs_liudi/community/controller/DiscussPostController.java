package com.cs_liudi.community.controller;


import com.cs_liudi.community.entity.DiscussPost;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.service.DiscussPostService;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String PublishDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtils.getJSONString(403,"您还没有登录，请重新登陆");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);
        return CommunityUtils.getJSONString(0,"恭喜！发布成功！");
    }
}
