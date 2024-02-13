package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.GoodsCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryMapper {
    GoodsCategory selectGoodsCategory(@Param("categoryId")int categoryId);

    List<GoodsCategory> selectGoodsCategoryList();
}
