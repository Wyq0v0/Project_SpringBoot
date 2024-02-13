package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserAdminService {
    Result getUserList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize);
    Result updateProfilePicture(int userId,MultipartFile image) throws IOException;
    Result updateUserInfo(User user);
}
