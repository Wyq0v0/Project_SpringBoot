package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Image;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentImageMapper {

    int insertCommentImage(Image image);
    int deleteCommentImage(@Param("commentId") int commentId);
    List<Image> selectImageList(@Param("commentId") int commentId);

}
