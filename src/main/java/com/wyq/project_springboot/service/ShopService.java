package com.wyq.project_springboot.service;

import com.alipay.api.AlipayApiException;
import com.wyq.project_springboot.entity.Goods;
import com.wyq.project_springboot.entity.GoodsOrder;
import com.wyq.project_springboot.entity.GoodsType;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.enumClass.GoodsOrderState;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ShopService {
    Result getGoods(int goodsId);

    Result addAddress(String name, String phoneNumber, String area, String addressDetail);

    Result getAddressList();

    Result getGoodsList(String selectItem,int selectCategory, String content, String sortBy, String sortOrder, int pageNum, int pageSize);

    Result getGoodsCategoryList();

    Result getOrderList(String selectItem, GoodsOrderState selectState, String content, String sortBy, String sortOrder, int pageNum, int pageSize);

    Result applyGoods(Goods goods, MultipartFile[] previewImages, MultipartFile[] detailImages, MultipartFile[] goodsTypeImages) throws IOException;

    Result getGoodsType(int goodsTypeId);

    Result addOrder(int goodsTypeId, int quantity,int addressId);

    Result getOrder(String orderNo);

    void payOrder(String orderNo, HttpServletResponse httpServletResponse) throws IOException;

    void payNotify(HttpServletRequest httpServletRequest) throws AlipayApiException;

    Result getSalesGoodsOrderList(int goodsId,String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize);

    Result deleteGoodsType(int goodsTypeId);
}
