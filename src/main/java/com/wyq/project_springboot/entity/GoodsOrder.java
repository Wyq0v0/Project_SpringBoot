package com.wyq.project_springboot.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.wyq.project_springboot.entity.enumClass.GoodsOrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsOrder {
    private String orderNo;//订单号
    private int userId;//用户ID
    private int goodsId;//商品ID
    private int goodsTypeId;//商品类型ID
    private String goodsName;//商品名称
    private String goodsTypeName;//商品类型名称
    private int quantity;//购买数量
    private BigDecimal unitPrice;//单价
    private BigDecimal totalPrice;//总价
    private String recipientName;//收件人姓名
    private String recipientPhoneNumber;//收件人手机号
    private String recipientAddress;//收件人地址
    private String trackingNumber;//运单号
    private String returnTrackingNumber;//退货运单号
    private GoodsOrderState state = GoodsOrderState.UNPAID;//订单状态
    private Date recordTime;//订单生成时间
    private Date shippedTime;//发货时间
    private Date returnTime;//退货时间
    private Date buyerSignedTime;//买家签收时间
    private Date sellerSignedTime;//卖家签收时间
    private Date paymentTime;
}
