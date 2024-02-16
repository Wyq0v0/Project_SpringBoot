package com.wyq.project_springboot.service.Impl;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.NoticeService;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

import static com.wyq.project_springboot.utils.RedisConstants.NOTICE_ALL_KEY;
import static com.wyq.project_springboot.utils.RedisConstants.NOTICE_PERSON_KEY;

@Service
@Transactional
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendSystemNotice() {
        return null;
    }

    @Override
    public Result getNoticeList(int pageNum, int pageSize) {

//            public Set<ZSetOperations.TypedTuple<String>> unionAndSortZSets(String key1, String key2, String destinationKey) {
//                // 获取ZSetOperations
//                ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
//
//                // 使用ZUNIONSTORE命令获取两个ZSET的并集并存储到目标ZSET
//                Long unionSize = zSetOperations.unionAndStore(key1, key2, destinationKey);
//
//                // 按分数从小到大获取并返回ZSET的成员及其分数
//                Set<ZSetOperations.TypedTuple<String>> unionResult = zSetOperations.rangeWithScores(destinationKey, 0, unionSize - 1);
//
//                return unionResult;
//            }
//
//            public static void main(String[] args) {
//                // 在你的Spring Boot应用中注入RedisTemplate
//
//                // 创建RedisZSetUnionExample实例
//                RedisZSetUnionExample zSetUnionExample = new RedisZSetUnionExample(redisTemplate);
//
//                // 定义两个ZSET的key和目标ZSET的key
//                String key1 = "zset1";
//                String key2 = "zset2";
//                String destinationKey = "unionZSet";
//
//                // 获取并集并排序
//                Set<ZSetOperations.TypedTuple<String>> unionResult = zSetUnionExample.unionAndSortZSets(key1, key2, destinationKey);
//
//                // 打印结果
//                for (ZSetOperations.TypedTuple<String> tuple : unionResult) {
//                    System.out.println("Member: " + tuple.getValue() + ", Score: " + tuple.getScore());
//                }
//            }
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        Set<String> noticeUnion = stringRedisTemplate.opsForZSet().union(NOTICE_PERSON_KEY + userId, NOTICE_ALL_KEY);

        return null;
    }
}
