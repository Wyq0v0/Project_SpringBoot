package com.wyq.project_springboot.dto.goods;

import com.wyq.project_springboot.entity.Goods;
import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDTO {
    private Goods goods;
    private User user;
}
