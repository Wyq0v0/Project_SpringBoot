package com.wyq.project_springboot.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.config.AliPayConfig;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.goods.GoodsDTO;
import com.wyq.project_springboot.dto.goods.GoodsTypeDTO;
import com.wyq.project_springboot.dto.goods.GoodsOrderDTO;
import com.wyq.project_springboot.entity.*;
import com.wyq.project_springboot.entity.enumClass.GoodsOrderState;
import com.wyq.project_springboot.mapper.*;
import com.wyq.project_springboot.service.ShopService;
import com.wyq.project_springboot.utils.SnowFlakeCompone;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.wyq.project_springboot.entity.enumClass.GoodsOrderState.PAID;
import static com.wyq.project_springboot.utils.AddressConstants.ADDRESS_TOTAL_MAX;
import static com.wyq.project_springboot.utils.AliPayConstant.*;
import static com.wyq.project_springboot.utils.ImageUploadConstant.*;
import static com.wyq.project_springboot.utils.RedisConstants.ADDRESS_KEY;

@Service
@Transactional
public class ShopServiceImpl implements ShopService {
    @Autowired
    private AliPayConfig aliPayConfig;
    @Autowired
    private SnowFlakeCompone snowFlakeCompone;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    @Autowired
    private GoodsOrderMapper goodsOrderMapper;
    @Autowired
    private GoodsPreviewImageMapper goodsPreviewImageMapper;
    @Autowired
    private GoodsDetailImageMapper goodsDetailImageMapper;
    @Autowired
    private GoodsTypeImageMapper goodsTypeImageMapper;
    @Autowired
    private GoodsTypeMapper goodsTypeMapper;
    @Override
    public Result getGoods(int goodsId) {
        Goods goods = goodsMapper.selectGoods(goodsId);
        if (goods == null) {
            return Result.error("商品不存在");
        }

        //查询商品类型
        List<GoodsType> goodsTypes = goodsTypeMapper.selectGoodsTypeList(goods.getId());
        goods.setGoodsTypes(goodsTypes);
        //查询商品预览图
        List<Image> previewImages = goodsPreviewImageMapper.selectImageList(goods.getId());
        goods.setPreviewImage(previewImages);
        //查询商品详情图
        List<Image> detailImages = goodsDetailImageMapper.selectImageList(goods.getId());
        goods.setDetailImage(detailImages);

        //根据商品中的用户id查询用户
        User user = userMapper.selectUserById(goods.getUserId());

        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setGoods(goods);
        goodsDTO.setUser(user);
        return Result.success(goodsDTO);
    }

    @Override
    public Result addAddress(String name, String phoneNumber, String area, String addressDetail) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        Address address = new Address();
        address.setName(name);
        address.setPhoneNumber(phoneNumber);
        address.setAddress(area + addressDetail);
        address.setUserId(userId);

        //从Redis中获取用户地址
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(ADDRESS_KEY + userId, 0, System.currentTimeMillis());

        //若地址总数不小于最大地址数量
        if(!(typedTuples.size() < ADDRESS_TOTAL_MAX)){
            return Result.success("添加失败，最多可拥有" + ADDRESS_TOTAL_MAX + "个地址！");
        }

        //向数据库中插入地址，并返回id
        userAddressMapper.insertAddress(address);

        //若用户地址为空，则给插入地址时，将地址的分数设置为当前时间，则为默认地址
        if(typedTuples == null || typedTuples.isEmpty()){
            stringRedisTemplate.opsForZSet().add(ADDRESS_KEY + userId,Integer.toString(address.getId()),System.currentTimeMillis());
        }else{
            stringRedisTemplate.opsForZSet().add(ADDRESS_KEY + userId,Integer.toString(address.getId()),0);
        }
        return Result.success();
    }

    @Override
    public Result getAddressList() {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        //从Redis中获取用户地址
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(ADDRESS_KEY + userId, 0, System.currentTimeMillis());

        if(typedTuples == null || typedTuples.isEmpty()){
            return Result.success();
        }

        //给addressIdList赋值并查询地址
        List<Integer> addressIdList = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            addressIdList.add(Integer.parseInt(typedTuple.getValue()));
        }
        List<Address> addresses = userAddressMapper.selectAddressList(addressIdList);

        return Result.success(addresses);
    }

    @Override
    public Result getGoodsList(String selectItem, int selectCategory, String content, String sortBy, String sortOrder, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //该对象作为搜索条件
        Goods selectGoods = new Goods();
        selectGoods.setCategory(selectCategory);

        PageHelper.startPage(pageNum, pageSize, sortBy + " " + sortOrder);
        Page<Goods> page = null;

        switch (selectItem) {
            case "goodsName":
                selectGoods.setName(content);
                page = (Page<Goods>) goodsMapper.selectGoodsListByCriteria(selectGoods);
                break;
            case "userId":
                selectGoods.setUserId(Integer.parseInt(content));
                page = (Page<Goods>) goodsMapper.selectGoodsListByCriteria(selectGoods);
                break;
            default:
                throw new RuntimeException();
        }
        //为momentListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为momentListDTO的momentDTOList赋值
        List<GoodsDTO> goodsDTOList = new ArrayList<>();
        for (Goods goods : page.getResult()) {
            GoodsDTO goodsDTO = new GoodsDTO();

            List<Image> images = goodsPreviewImageMapper.selectImageList(goods.getId());
            goods.setPreviewImage(images);

            goodsDTO.setGoods(goods);

            User user = userMapper.selectUserById(goods.getUserId());

            goodsDTO.setUser(user);

            goodsDTOList.add(goodsDTO);
        }
        listDTO.setListData(goodsDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result getGoodsCategoryList() {
        ListDTO listDTO = new ListDTO();
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectGoodsCategoryList();
        listDTO.setListData(goodsCategories);
        return Result.success(listDTO);
    }

    @Override
    public Result getOrderList(String selectItem, GoodsOrderState selectState, String content, String sortBy, String sortOrder, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        //该对象作为搜索条件
        GoodsOrder selectGoodsOrder = new GoodsOrder();
        selectGoodsOrder.setState(selectState);
        selectGoodsOrder.setUserId(userId);

        PageHelper.startPage(pageNum, pageSize, sortBy + " " + sortOrder);
        Page<GoodsOrder> page = null;

        switch (selectItem) {
            case "goodsName":

                break;
            case "orderNo":
                selectGoodsOrder.setOrderNo(content);
                page = (Page<GoodsOrder>) goodsOrderMapper.selectOrderByCriteria(selectGoodsOrder);
                break;
            default:
                throw new RuntimeException();
        }
        //为listDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为listDTO的Data赋值
        List<GoodsOrderDTO> goodsOrderDTOList = new ArrayList<>(pageSize);
        for(GoodsOrder goodsOrder: page.getResult()){
            GoodsOrderDTO goodsOrderDTO = goodsOrderToGoodsOrderDTO(goodsOrder);
            goodsOrderDTOList.add(goodsOrderDTO);
        }
        listDTO.setListData(goodsOrderDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result applyGoods(Goods goods, MultipartFile[] previewImages, MultipartFile[] detailImages, MultipartFile[] goodsTypeImages) throws IOException {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        goods.setUserId(userId);
        //将商品写入数据库，并返回id
        goodsMapper.insertGoods(goods);

        //保存商品预览图片
        for (MultipartFile image : previewImages) {
            //获取原始文件名
            String fileName = image.getOriginalFilename();
            //获取文件后缀
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            //使用UUID获取不重复的随机文件名
            fileName = UUID.randomUUID().toString() + fileSuffix;

            //将图片文件保存到文件目录下
            String fileRelativePath = GOODS_PREVIEW_IMAGE_PREFIX + fileName;
            String finalAbsolutePath = IMAGE_UPLOAD_PREFIX + fileRelativePath;
            image.transferTo(new File(finalAbsolutePath));

            //创建图片对象
            Image img = new Image();
            img.setDependId(goods.getId());
            img.setImagePath(fileRelativePath);
            img.setImageSize(image.getSize());

            //向数据库插入图片
            goodsPreviewImageMapper.insertImage(img);
        }

        //保存商品详情图片
        for (MultipartFile image : detailImages) {
            //获取原始文件名
            String fileName = image.getOriginalFilename();
            //获取文件后缀
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            //使用UUID获取不重复的随机文件名
            fileName = UUID.randomUUID().toString() + fileSuffix;

            //将图片文件保存到文件目录下
            String fileRelativePath = GOODS_DETAIL_IMAGE_PREFIX + fileName;
            String finalAbsolutePath = IMAGE_UPLOAD_PREFIX + fileRelativePath;
            image.transferTo(new File(finalAbsolutePath));

            //创建图片对象
            Image img = new Image();
            img.setDependId(goods.getId());
            img.setImagePath(fileRelativePath);
            img.setImageSize(image.getSize());

            //向数据库插入图片
            goodsDetailImageMapper.insertImage(img);
        }

        //获取商品类型列表
        List<GoodsType> goodsTypes = goods.getGoodsTypes();

        //保存商品类型图片
        for (int i = 0; i < goodsTypeImages.length; i++) {
            //将图片对应的商品类型保存入数据库，并获取id
            GoodsType goodsType = goodsTypes.get(i);
            goodsType.setGoodsId(goods.getId());
            goodsTypeMapper.insertGoodsType(goodsType);

            //获取原始文件名
            String fileName = goodsTypeImages[i].getOriginalFilename();
            //获取文件后缀
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            //使用UUID获取不重复的随机文件名
            fileName = UUID.randomUUID().toString() + fileSuffix;

            //将图片文件保存到文件目录下
            String fileRelativePath = GOODS_TYPE_IMAGE_PREFIX + fileName;
            String finalAbsolutePath = IMAGE_UPLOAD_PREFIX + fileRelativePath;
            goodsTypeImages[i].transferTo(new File(finalAbsolutePath));

            //创建图片对象
            Image img = new Image();
            img.setDependId(goodsType.getId());
            img.setImagePath(fileRelativePath);
            img.setImageSize(goodsTypeImages[i].getSize());

            //向数据库插入图片
            goodsTypeImageMapper.insertImage(img);
        }

        return Result.success();
    }

    @Override
    public Result getGoodsType(int goodsTypeId) {
        GoodsTypeDTO goodsTypeDTO = new GoodsTypeDTO();

        GoodsType goodsType = goodsTypeMapper.selectGoodsType(goodsTypeId);
        Goods goods = goodsMapper.selectGoods(goodsType.getGoodsId());

        goodsTypeDTO.setGoodsType(goodsType);
        goodsTypeDTO.setGoods(goods);

        return Result.success(goodsTypeDTO);
    }

    @Override
    public Result addOrder(int goodsTypeId, int quantity,int addressId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        //判断该商品类型是否还有库存
        GoodsType goodsType = goodsTypeMapper.selectGoodsType(goodsTypeId);
        //如果商品类型不存在或库存小于购买量
        if(goodsType == null || goodsType.getStorage() < quantity){
            return Result.success("该商品类型库存不足");
        }

        //添加订单
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setUserId(userId);
        goodsOrder.setGoodsTypeId(goodsTypeId);
        goodsOrder.setQuantity(quantity);
        goodsOrder.setUnitPrice(goodsType.getPrice());

        Goods goods = goodsMapper.selectGoods(goodsType.getGoodsId());
        goodsOrder.setGoodsId(goods.getId());
        goodsOrder.setGoodsName(goods.getName());

        goodsOrder.setGoodsTypeId(goodsType.getId());
        goodsOrder.setGoodsTypeName(goodsType.getName());

        BigDecimal quantityBigDecimal = new BigDecimal(quantity);
        goodsOrder.setTotalPrice(goodsType.getPrice().multiply(quantityBigDecimal));

        Address address = userAddressMapper.selectAddress(addressId);
        goodsOrder.setRecipientName(address.getName());
        goodsOrder.setRecipientPhoneNumber(address.getPhoneNumber());
        goodsOrder.setRecipientAddress(address.getAddress());

        //雪花算法生成订单号
        String orderNo = Long.toString(snowFlakeCompone.getInstance().nextId());
        goodsOrder.setOrderNo(orderNo);

        //TODO 减少库存


        goodsOrderMapper.insertOrder(goodsOrder);

        //将订单号返回给客户端
        return Result.success(goodsOrder);
    }


    @Override
    public Result getOrder(String orderNo) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        GoodsOrder goodsOrder = goodsOrderMapper.selectOrder(orderNo);

        //订单是否存在
        if(goodsOrder == null){
            return Result.error("订单不存在");
        }
        //订单是否属于该用户
        if(goodsOrder.getUserId() != userId){
            return Result.error("该订单不属于你，无权查看");
        }

        GoodsOrderDTO goodsOrderDTO = goodsOrderToGoodsOrderDTO(goodsOrder);

        return Result.success(goodsOrderDTO);
    }

    @Override
    public void payOrder(String orderNo, HttpServletResponse httpServletResponse) throws IOException {
        //查询订单
        GoodsOrder goodsOrder = goodsOrderMapper.selectOrder(orderNo);
        if(goodsOrder == null){
            return;
        }

        GoodsType goodsType = goodsTypeMapper.selectGoodsType(goodsOrder.getGoodsTypeId());
        Goods goods = goodsMapper.selectGoods(goodsType.getGoodsId());

        //创建client，调用支付宝API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,aliPayConfig.getAppId(), aliPayConfig.getAppPrivateKey(),FORMAT,CHARSETE, aliPayConfig.getAlipayPublicKey(),SIGN_TYPE);
        //创建Request并设置参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no",goodsOrder.getOrderNo());//订单编号
        jsonObject.put("total_amount",goodsOrder.getTotalPrice());//订单总价
        jsonObject.put("subject",goods.getName() + " " + goodsType.getName());//支付名称
        jsonObject.put("product_code","FAST_INSTANT_TRADE_PAY");
        request.setBizContent(jsonObject.toString());
        request.setReturnUrl(RETURN_URL);

        //执行请求，将获取到的响应结果返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        httpServletResponse.setContentType("text/html;charset=" + CHARSETE);
        httpServletResponse.getWriter().write(form);
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
    }

    @Override
    public void payNotify(HttpServletRequest httpServletRequest) throws AlipayApiException {
        if(httpServletRequest.getParameter("trade_status").equals("TRADE_SUCCESS")){
            HashMap<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
            for(String name:requestParams.keySet()){
                params.put(name,httpServletRequest.getParameter(name));
            }
            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), CHARSETE);
            if(checkSignature){
                //更新订单信息
                goodsOrderMapper.updateOrderState(params.get("out_trade_no"),PAID);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date paymentTime = simpleDateFormat.parse(params.get("gmt_payment"));
                    goodsOrderMapper.updateOrderPaymentTime(params.get("out_trade_no"),paymentTime);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Result getSalesGoodsOrderList(Integer goodsId,String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        //判断是否为商品卖家本人查看
        Goods goods = goodsMapper.selectGoods(goodsId);
        if(!userId.equals(goods.getUserId())){
            return Result.error("无权查看此商品订单");
        }

        ListDTO listDTO = new ListDTO();
        GoodsOrder selectGoodsOrder = new GoodsOrder();
        selectGoodsOrder.setGoodsId(goodsId);

        PageHelper.startPage(pageNum, pageSize, sortBy + " " + sortOrder);
        Page<GoodsOrder> page = null;

        switch (selectItem) {
            case "goodsTypeName":
                selectGoodsOrder.setGoodsTypeName(content);
                page = (Page<GoodsOrder>) goodsOrderMapper.selectOrderByCriteria(selectGoodsOrder);
                break;
            case "orderNo":
                selectGoodsOrder.setOrderNo(content);
                page = (Page<GoodsOrder>) goodsOrderMapper.selectOrderByCriteria(selectGoodsOrder);
                break;
            default:
                throw new RuntimeException();
        }
        //为listDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        List<GoodsOrderDTO> goodsOrderDTOList = new ArrayList<>(pageSize);
        for (GoodsOrder goodsOrder : page.getResult()) {
            GoodsOrderDTO goodsOrderDTO = goodsOrderToGoodsOrderDTO(goodsOrder);
            goodsOrderDTOList.add(goodsOrderDTO);
        }
        listDTO.setListData(goodsOrderDTOList);

        return Result.success(listDTO);
    }

    private GoodsOrderDTO goodsOrderToGoodsOrderDTO(GoodsOrder goodsOrder){
        GoodsOrderDTO goodsOrderDTO = new GoodsOrderDTO();
        goodsOrderDTO.setGoodsOrder(goodsOrder);

        GoodsType goodsType = goodsTypeMapper.selectGoodsType(goodsOrder.getGoodsTypeId());
        goodsOrderDTO.setGoodsTypeImagePath(goodsType.getImage().getImagePath());

        return goodsOrderDTO;
    }
}
