package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {
    UserInfo selectUserInfo(@Param("userId")int userId);
    int insertUserInfo(UserInfo userInfo);
    int updateUserInfo(UserInfo userInfo);
}
