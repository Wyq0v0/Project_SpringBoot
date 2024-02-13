package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Goods;
import com.wyq.project_springboot.entity.GoodsType;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.enumClass.GoodsOrderState;
import com.wyq.project_springboot.service.ShopService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/getGoods")
    public Result getGoods(@Min(1) Integer goodsId){
        return shopService.getGoods(goodsId);
    }

    @PostMapping("/applyGoods")
    public Result addGoods(Goods goods, MultipartFile[] previewImages,MultipartFile[] detailImages,MultipartFile[] goodsTypeImages) throws IOException {
        return shopService.applyGoods(goods,previewImages,detailImages,goodsTypeImages);
    }

    @PostMapping("/addAddress")
    public Result addAddress(String name,String phoneNumber,String area,String addressDetail){
        return shopService.addAddress(name,phoneNumber,area,addressDetail);
    }

    @GetMapping("/getAddressList")
    public Result getAddressList(){
        return shopService.getAddressList();
    }

    @GetMapping("/getGoodsList")
    public Result getGoodsList(@RequestParam String selectItem, @RequestParam Integer selectCategory,String content, @RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                               @Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize){
        return shopService.getGoodsList(selectItem,selectCategory,content,sortBy, sortOrder, pageNum, pageSize);
    }

    @GetMapping("/getGoodsCategoryList")
    public Result getGoodsCategoryList(){
        return shopService.getGoodsCategoryList();
    }

    @GetMapping("/getOrderList")
    public Result getOrderList(@RequestParam String selectItem, GoodsOrderState selectState, String content, @RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                               @Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize){
        return shopService.getOrderList(selectItem,selectState,content,sortBy,sortOrder,pageNum,pageSize);
    }

    @GetMapping("/getGoodsType")
    public Result getGoodsType(Integer goodsTypeId){
        return shopService.getGoodsType(goodsTypeId);
    }

    @PostMapping("/addOrder")
    public Result addOrder(Integer goodsTypeId,Integer quantity,Integer addressId){
        return shopService.addOrder(goodsTypeId,quantity,addressId);
    }

    @GetMapping("/getOrder")
    public Result getOrder(String orderNo){
        return shopService.getOrder(orderNo);
    }

    @GetMapping("/getSalesGoodsOrderList")
    public Result getSalesGoodsOrderList(Integer goodsId,@RequestParam String selectItem,String content,@RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                         @Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize){
        return shopService.getSalesGoodsOrderList(goodsId,selectItem,content,sortBy, sortOrder, pageNum, pageSize);
    }

}
