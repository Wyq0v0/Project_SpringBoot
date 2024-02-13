package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper {
    int insertGoods(Goods goods);
    Goods selectGoods(@Param("goodsId")int goodsId);
    List<Goods> selectGoodsListByCriteria(Goods goods);
}
