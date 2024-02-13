package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    Comment selectComment(@Param("commentId")int commentId);
    List<Comment> selectCommentList(@Param("momentId") int momentId);
    int insertComment(Comment comment);
    int deleteComment(@Param("userId") int userId, @Param("commentId") int commentId);
    int deleteCommentByMomentId(@Param("momentId")int momentId);
    int addCommentThumbsUpCount(@Param("commentId")int commentId);
    int subCommentThumbsUpCount(@Param("commentId")int commentId);

}
