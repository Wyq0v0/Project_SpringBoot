package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.DeleteState;
import com.wyq.project_springboot.entity.enumClass.GoodsState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    private int id;
    private int userId;
    private String name;
    private String detail;
    private int category;
    private int salesVolume;
    private GoodsState state = GoodsState.UNAUDITED;
    private List<GoodsType> goodsTypes;
    private List<Image> previewImage;
    private List<Image> detailImage;
    private DeleteState isDelete = DeleteState.UNDELETED;
}
