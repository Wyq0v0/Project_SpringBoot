package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.GoodsType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsTypeMapper {
    int insertGoodsType(GoodsType goodsType);
    GoodsType selectGoodsType(@Param("goodsTypeId")int goodsTypeId);
    List<GoodsType> selectGoodsTypeList(@Param("goodsId")int goodsId);
}
