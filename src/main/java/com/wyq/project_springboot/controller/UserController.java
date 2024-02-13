package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.service.UserService;
import com.wyq.project_springboot.utils.MD5Util;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/getUser")
    public Result getUser(@Min(1)Integer userId){
        return userService.getUser(userId);
    }

    @GetMapping("/getUserInfo")
    public Result getUserInfo(@Min(1)Integer userId){
        return userService.getUserInfo(userId);
    }

    @PutMapping("/updateProfilePicture")
    public Result updateProfilePicture(MultipartFile image) throws IOException {
        return userService.updateProfilePicture(image);
    }

    @PutMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody User user){
        return userService.updateUserInfo(user);
    }

    @PatchMapping("/updatePassword")
    public Result updatePassword(@Pattern(regexp = "^\\S{6,16}$") String oldPassword, @Pattern(regexp = "^\\S{6,16}$") String newPassword,@RequestHeader("Authorization")String token){

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        User user = userService.findUserById(userId);

        //判断密码是否正确
        if (user.getPassword().equals(MD5Util.setPasswordToMD5(oldPassword))) {

            newPassword = MD5Util.setPasswordToMD5(newPassword);

            //删除redis中该用户的token
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            stringStringValueOperations.getOperations().delete(token);

            return userService.updatePassword(newPassword);

        } else {
            return Result.error("原密码错误");
        }
    }

    @PostMapping("/signIn")
    public Result signIn(){
        return userService.signIn();
    }

    @GetMapping("/getExpDetail")
    public Result getExpDetail(){
        return userService.getExpDetail();
    }

    @GetMapping("/getExpTaskList")
    public Result getExpTaskList(){
        System.out.println(1);
        return userService.getExpTaskList();
    }
}
