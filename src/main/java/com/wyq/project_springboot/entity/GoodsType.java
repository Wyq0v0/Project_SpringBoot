package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.DeleteState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsType {
    private int id;
    private int goodsId;
    private String name;
    private BigDecimal price;
    private Image image;
    private int storage;
    private DeleteState isDelete = DeleteState.UNDELETED;
}
