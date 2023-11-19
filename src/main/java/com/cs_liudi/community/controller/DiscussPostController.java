package com.cs_liudi.community.controller;


import com.cs_liudi.community.entity.*;
import com.cs_liudi.community.service.CommentService;
import com.cs_liudi.community.service.DiscussPostService;
import com.cs_liudi.community.service.UserService;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

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

    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPostDetail(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost discussPost = discussPostService.getDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(discussPost.getCommentCount());

        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVOList = new ArrayList<>();
        if (commentList != null){
            for(Comment comment:commentList){
                Map<String,Object> commentVO = new HashMap<>();
                commentVO.put("comment",comment);
                commentVO.put("user",userService.findUserById(comment.getUserId()));
                List<Map<String,Object>> replyVOList = new ArrayList<>();
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENTS, comment.getId(), 0, Integer.MAX_VALUE);
                if (replyList != null){
                    for(Comment reply:replyList){
                        Map<String,Object> replyVO = new HashMap<>();
                        replyVO.put("reply",reply);
                        replyVO.put("user",userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVO.put("target",target);
                        replyVOList.add(replyVO);
                    }
                }
                commentVO.put("replyList",replyVOList);
                int replyCount = commentService.findCommentsRows(ENTITY_TYPE_COMMENTS, comment.getId());
                commentVO.put("replyCount",replyCount);
                commentVOList.add(commentVO);
            }
        }
        model.addAttribute("commentList",commentVOList);
        return "site/discuss-detail";
    }

}
