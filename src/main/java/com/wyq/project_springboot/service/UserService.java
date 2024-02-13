package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    Result getUser(int userId);

    Result getUserInfo(int userId);

    Result updateProfilePicture(MultipartFile image) throws IOException;

    Result updateUserInfo(User user);

    User findUserById(int userId);

    Result updatePassword(String newPasssword);

    Result signIn();

    Result getExpDetail();

    Result getExpTaskList();
}
