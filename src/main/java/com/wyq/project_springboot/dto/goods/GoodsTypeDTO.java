package com.wyq.project_springboot.dto.goods;

import com.wyq.project_springboot.entity.Goods;
import com.wyq.project_springboot.entity.GoodsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsTypeDTO {
    private GoodsType goodsType;
    private Goods goods;
}
