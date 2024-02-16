package com.wyq.project_springboot.service.Impl;

import com.wyq.project_springboot.entity.Notice;
import com.wyq.project_springboot.entity.NoticeRelation;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.entity.enumClass.NoticeType;
import com.wyq.project_springboot.mapper.NoticeMapper;
import com.wyq.project_springboot.mapper.NoticeRelationMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.service.NoticeAdminService;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.wyq.project_springboot.utils.RedisConstants.NOTICE_ALL_KEY;
import static com.wyq.project_springboot.utils.RedisConstants.NOTICE_PERSON_KEY;

@Service
@Transactional
@Slf4j
public class NoticeAdminServiceImpl implements NoticeAdminService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private NoticeRelationMapper noticeRelationMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public Result sendAdminNotice(String title, String content, NoticeType type, int receiverId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        Notice notice = new Notice();
        notice.setAdminId(userId);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType(type);

        //保存该通知，notice会获取到返回的id
        noticeMapper.insertNotice(notice);

        //识别该通知类型
        switch (type){
            case ALL:
                //写给Redis中全体通知ZSet
                stringRedisTemplate.opsForZSet().add(NOTICE_ALL_KEY,Integer.toString(notice.getId()),System.currentTimeMillis());
                break;
            case PERSON:
                User user = userMapper.selectUserById(receiverId);
                if(user != null) {

                    //保存通知关系
                    NoticeRelation noticeRelation = new NoticeRelation();
                    noticeRelation.setNoticeId(notice.getId());
                    noticeRelation.setReceiverId(receiverId);
                    noticeRelationMapper.insertNoticeRelation(noticeRelation);

                    //写给Redis中接收者的收件箱
                    stringRedisTemplate.opsForZSet().add(NOTICE_PERSON_KEY + receiverId, Integer.toString(notice.getId()), System.currentTimeMillis());
                }else {
                    return Result.error("该用户ID不存在");
                }
                break;
        }

        return Result.success();
    }
}
