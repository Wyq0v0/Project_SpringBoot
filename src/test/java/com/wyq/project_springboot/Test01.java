package com.wyq.project_springboot;

import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.goods.GoodsOrderDTO;
import com.wyq.project_springboot.entity.GoodsOrder;
import com.wyq.project_springboot.entity.GoodsType;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.enumClass.GoodsOrderState;
import com.wyq.project_springboot.mapper.GoodsOrderMapper;
import com.wyq.project_springboot.mapper.GoodsTypeMapper;
import com.wyq.project_springboot.service.ShopService;
import com.wyq.project_springboot.utils.SnowFlake;
import com.wyq.project_springboot.utils.SnowFlakeCompone;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

import static com.wyq.project_springboot.utils.RedisConstants.NOTICE_ALL_KEY;
import static com.wyq.project_springboot.utils.RedisConstants.NOTICE_PERSON_KEY;

@SpringBootTest
public class Test01 {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SnowFlakeCompone snowFlakeCompone;
    @Autowired
    private ShopService shopService;
    @Test
    void fun01(){
        Result orderList = shopService.getOrderList("orderNo", GoodsOrderState.UNPAID, "", "record_time", "desc", 1, 5);
        ListDTO data = (ListDTO) orderList.getData();
        List<GoodsOrderDTO> listData = data.getListData();
        for (GoodsOrderDTO i:listData) {
            System.out.println(i);
        }
    }
}
