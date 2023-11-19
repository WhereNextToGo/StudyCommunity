package com.cs_liudi.community.dao;

import com.cs_liudi.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType ,int entityId,int offset,int limit);

    int countCommentRows(int entityType ,int entityId);

    int insertComment(Comment comment);

}
