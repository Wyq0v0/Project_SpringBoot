package com.wyq.project_springboot.service.Impl;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.entity.UserInfo;
import com.wyq.project_springboot.mapper.UserInfoMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.service.UserAccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class UserAccessServiceImpl implements UserAccessService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    public User findUserByPhoneNumber(String phoneNumber){
        return userMapper.selectUserByPhoneNumber(phoneNumber);
    }

    @Override
    public Result register(User user) {
        //该方法会将新添加的用户ID返回给user对象
        userMapper.insertUser(user);

        //为用户创建一个用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfoMapper.insertUserInfo(userInfo);

        return Result.success();
    }
}
