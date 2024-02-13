package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.service.UserAdminService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/admin/userAdmin")
public class UserAdminController {
    @Autowired
    private UserAdminService userAdminService;
    @GetMapping("/getUserList")
    public Result getUserList(@RequestParam String selectItem, String content, @RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                @Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize) {
        return userAdminService.getUserList(selectItem,content,sortBy, sortOrder, pageNum, pageSize);
    }

    @PutMapping("/updateProfilePicture")
    public Result updateProfilePicture(@Min(1)Integer userId,MultipartFile image) throws IOException {
        return userAdminService.updateProfilePicture(userId,image);
    }

    @PutMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody User user){
        return userAdminService.updateUserInfo(user);
    }

}
