package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Image;
import org.apache.ibatis.annotations.Param;

public interface GoodsTypeImageMapper {
    int insertImage(Image image);
    int deleteImage(@Param("goodsTypeId") int goodsTypeId);
    Image selectGoodsTypeImage(@Param("goodsTypeId")int goodsTypeId);
}
