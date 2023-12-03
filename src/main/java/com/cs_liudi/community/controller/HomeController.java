package com.cs_liudi.community.controller;

import com.cs_liudi.community.entity.DiscussPost;
import com.cs_liudi.community.entity.Page;
import com.cs_liudi.community.entity.User;
import com.cs_liudi.community.service.DiscussPostService;
import com.cs_liudi.community.service.UserService;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
//    @ResponseBody
    public String getHomePage(Model model, Page page){
        int rows = discussPostService.findDiscussPostRows(0);
        page.setRows(rows);
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        if (list.size() != 0){
        List<HashMap<String,Object>> discussPosts = new ArrayList<>();
        for (DiscussPost post : list) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("post",post);
            User user = userService.findUserById(post.getUserId());
            hashMap.put("user",user);
            discussPosts.add(hashMap);
        }
        model.addAttribute("discussPosts",discussPosts);
        }
//        return discussPosts;
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }
}
