package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;

public interface UserAccessService {
    User findUserByPhoneNumber(String phoneNumber);
    Result register(User user);
}
