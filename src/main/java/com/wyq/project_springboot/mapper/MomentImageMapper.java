package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Image;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MomentImageMapper {

    int insertImage(Image image);
    int deleteMomentImage(@Param("momentId") int momentId);
    List<Image> selectImageList(@Param("momentId") int momentId);
}
