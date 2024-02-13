package com.wyq.project_springboot.dto.goods;

import com.wyq.project_springboot.entity.GoodsOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsOrderDTO {
    private GoodsOrder goodsOrder;
    private String goodsTypeImagePath;
}
