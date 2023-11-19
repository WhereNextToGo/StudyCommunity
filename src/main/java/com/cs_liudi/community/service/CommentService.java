package com.cs_liudi.community.service;

import com.cs_liudi.community.dao.CommentMapper;
import com.cs_liudi.community.entity.Comment;
import com.cs_liudi.community.entity.CommunityConstant;
import com.cs_liudi.community.util.CommunityUtils;
import com.cs_liudi.community.util.SensitiveFilter;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType,int entityId,int offset,int limit){
        return commentMapper.selectCommentByEntity(entityType,entityId,offset,limit);
    }
    public int findCommentsRows(int entityType,int entityId){
        return commentMapper.countCommentRows(entityType,entityId);
    }
    @Transactional(isolation = Isolation.READ_UNCOMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("参数错误");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.countCommentRows(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }
}
