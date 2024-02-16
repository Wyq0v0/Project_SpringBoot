package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.ShopAdminService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/admin/shopAdmin")
public class ShopAdminController {
    @Autowired
    private ShopAdminService shopAdminService;
    @GetMapping("/getGoodsList")
    public Result getMomentList(@RequestParam String selectItem,String content,@RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                @Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize) {
        return shopAdminService.getGoodsList(selectItem,content,sortBy, sortOrder, pageNum, pageSize);
    }

    @GetMapping("/getUnauditedGoodsList")
    public Result getUnauditedGoodsList(@Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize) {
        return shopAdminService.getUnauditedGoodsList(pageNum, pageSize);
    }

    @DeleteMapping("/deleteGoods")
    public Result deleteGoods(@Min(1)Integer goodsId){
        return shopAdminService.deleteGoods(goodsId);
    }

    @PutMapping("/rejectGoodsApply")
    public Result rejectGoodsApply(Integer goodsId){
        return shopAdminService.rejectGoodsApply(goodsId);
    }

    @PutMapping("/passGoodsApply")
    public Result passGoodsApply(Integer goodsId){
        return shopAdminService.passGoodsApply(goodsId);
    }
}
