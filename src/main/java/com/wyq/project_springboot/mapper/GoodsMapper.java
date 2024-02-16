package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Goods;
import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.enumClass.DeleteState;
import com.wyq.project_springboot.entity.enumClass.GoodsState;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper {
    int insertGoods(Goods goods);
    Goods selectGoods(@Param("goodsId")int goodsId);
    List<Goods> selectGoodsListByCriteria(Goods goods);
    int updateGoodsDeleteState(@Param("goodsId")int goodsId, @Param("deleteState") DeleteState deleteState);
    List<Goods> selectUnauditedGoodsList();
    int updateGoodsState(@Param("goodsId")int goodsId,@Param("state") GoodsState state);
    List<Goods> selectGoodsListByAccountName(@Param("accountName")String accountName);
}
