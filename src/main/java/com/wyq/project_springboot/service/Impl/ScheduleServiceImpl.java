package com.wyq.project_springboot.service.Impl;

import com.wyq.project_springboot.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.wyq.project_springboot.utils.RedisConstants.EXP_TASK_KEY;

@Service
@Transactional
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanExpTask(){
        //删除所有用户当日经验任务数据
        stringRedisTemplate.delete(stringRedisTemplate.keys(EXP_TASK_KEY + "*"));
    }
}
