package com.wyq.project_springboot.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.goods.GoodsDTO;
import com.wyq.project_springboot.dto.moment.MomentDTO;
import com.wyq.project_springboot.entity.Goods;
import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.entity.enumClass.DeleteState;
import com.wyq.project_springboot.entity.enumClass.GoodsState;
import com.wyq.project_springboot.mapper.GoodsMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.service.ShopAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.wyq.project_springboot.entity.enumClass.DeleteState.DELETED;
import static com.wyq.project_springboot.entity.enumClass.DeleteState.UNDELETED;
import static com.wyq.project_springboot.entity.enumClass.GoodsState.AUDITED;
import static com.wyq.project_springboot.entity.enumClass.GoodsState.REJECTED;


@Service
@Transactional
@Slf4j
public class ShopAdminServiceImpl implements ShopAdminService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public Result getGoodsList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //该对象作为搜索条件
        Goods selectGoods = new Goods();
        selectGoods.setState(AUDITED);
        selectGoods.setIsDelete(UNDELETED);

        PageHelper.startPage(pageNum, pageSize, sortBy + " " + sortOrder);
        Page<Goods> page = null;

        switch (selectItem) {
            case "name":
                selectGoods.setName(content);
                page = (Page<Goods>) goodsMapper.selectGoodsListByCriteria(selectGoods);
                break;
            case "userId":
                selectGoods.setUserId(Integer.parseInt(content));
                page = (Page<Goods>) goodsMapper.selectGoodsListByCriteria(selectGoods);
                break;
            case "accountName":
                page = (Page<Goods>) goodsMapper.selectGoodsListByAccountName(content);
                break;
            default:
                throw new RuntimeException();
        }

        listDTO.setTotal(page.getTotal());

        List<GoodsDTO> goodsDTOList = new ArrayList<>(pageSize);
        for (Goods goods : page.getResult()) {
            GoodsDTO goodsDTO = new GoodsDTO();
            goodsDTO.setGoods(goods);
            goodsDTO.setUser(userMapper.selectUserById(goods.getUserId()));
            goodsDTOList.add(goodsDTO);
        }
        listDTO.setListData(goodsDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result getUnauditedGoodsList(int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        PageHelper.startPage(pageNum, pageSize);
        Page<Goods> page = (Page<Goods>) goodsMapper.selectUnauditedGoodsList();

        List<GoodsDTO> goodsDTOList = new ArrayList<>(pageSize);
        for(Goods goods: page.getResult()){
            GoodsDTO goodsDTO = new GoodsDTO();
            goodsDTO.setGoods(goods);
            goodsDTO.setUser(userMapper.selectUserById(goods.getUserId()));
            goodsDTOList.add(goodsDTO);
        }

        listDTO.setTotal(page.getTotal());
        listDTO.setListData(goodsDTOList);
        return Result.success(listDTO);
    }

    @Override
    public Result rejectGoodsApply(int goodsId) {
        Goods goods = goodsMapper.selectGoods(goodsId);
        if(goods == null){
            return Result.error("该商品不存在");
        }

        goodsMapper.updateGoodsState(goodsId,REJECTED);
        return Result.success();
    }

    @Override
    public Result passGoodsApply(int goodsId) {
        Goods goods = goodsMapper.selectGoods(goodsId);
        if(goods == null){
            return Result.error("该商品不存在");
        }

        goodsMapper.updateGoodsState(goodsId, AUDITED);
        return Result.success();
    }

    @Override
    public Result deleteGoods(int goodsId) {
        Goods goods = goodsMapper.selectGoods(goodsId);
        //判断商品是否存在
        if(goods == null){
            return Result.error("商品不存在");
        }
        //将商品标记为删除
        goodsMapper.updateGoodsDeleteState(goodsId,DELETED);
        return Result.success();
    }

}
