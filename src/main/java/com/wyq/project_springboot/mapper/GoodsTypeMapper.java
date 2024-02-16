package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.GoodsType;
import com.wyq.project_springboot.entity.enumClass.DeleteState;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsTypeMapper {
    int insertGoodsType(GoodsType goodsType);
    GoodsType selectGoodsType(@Param("goodsTypeId")int goodsTypeId);
    List<GoodsType> selectGoodsTypeList(@Param("goodsId")int goodsId);
    int updateGoodsTypeDeleteState(@Param("goodsTypeId")int goodsTypeId, @Param("deleteState")DeleteState deleteState);
}
