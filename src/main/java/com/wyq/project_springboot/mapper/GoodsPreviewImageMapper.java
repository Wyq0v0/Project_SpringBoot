package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Image;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsPreviewImageMapper {
    int insertImage(Image image);
    int deleteImage(@Param("goodsId") int goodsId);

    List<Image> selectImageList(@Param("goodsId") int goodsId);
}
