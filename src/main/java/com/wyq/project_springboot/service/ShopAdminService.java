package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;

public interface ShopAdminService {
    Result getGoodsList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize);
    Result getUnauditedGoodsList(int pageNum, int pageSize);
    Result rejectGoodsApply(int goodsId);
    Result passGoodsApply(int goodsId);

    Result deleteGoods(int goodsId);
}
