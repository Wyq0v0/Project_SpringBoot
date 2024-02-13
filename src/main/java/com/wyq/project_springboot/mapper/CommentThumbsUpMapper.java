package com.wyq.project_springboot.mapper;

import org.apache.ibatis.annotations.Param;

public interface CommentThumbsUpMapper {

    int insertCommentThumbsUp(@Param("userId")int userId, @Param("commentId")int commentId);
    int deleteCommentThumbsUp(@Param("userId")int userId,@Param("commentId")int commentId);
    int deleteCommentThumbsUpByCommentId(@Param("commentId")int commentId);
    int selectCommentThumbsUp(@Param("userId")int userId,@Param("commentId")int commentId);

}
