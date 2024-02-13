package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.GoodsOrder;
import com.wyq.project_springboot.entity.enumClass.GoodsOrderState;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface GoodsOrderMapper {
    int insertOrder(GoodsOrder goodsOrder);
    GoodsOrder selectOrder(@Param("orderNo")String orderNo);
    List<GoodsOrder> selectUserOrderList(@Param("userId")int userId,@Param("recordTime")int recordTime,@Param("offset")int offset,@Param("pageSize")int pageSize);
    int updateOrderState(@Param("orderNo")String orderNo,@Param("state")GoodsOrderState goodsOrderState);
    int updateOrderPaymentTime(@Param("orderNo")String orderNo,@Param("paymentTime")Date paymentTime);
    List<GoodsOrder> selectOrderByCriteria(GoodsOrder goodsOrder);
}
