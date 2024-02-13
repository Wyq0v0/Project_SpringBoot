package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User selectUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    User selectUserById(@Param("id") int id);

    int insertUser(User user);

    List<User> selectUserListByAccountName(@Param("accountName") String accountName);

    int updateProfilePicture(@Param("userId") int userId, @Param("fileRelativePath") String fileRelativePath);

    int updateUser(User user);

    int updatePassword(@Param("userId") int userId, @Param("password") String password);

    int updateUserWithPassword(User user);

    int updateExperience(@Param("userId")int userId,@Param("value")int value);

}
