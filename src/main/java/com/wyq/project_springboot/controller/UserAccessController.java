package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.service.UserAccessService;
import com.wyq.project_springboot.utils.JwtUtil;
import com.wyq.project_springboot.utils.MD5Util;
import com.wyq.project_springboot.utils.RedisConstants;
import com.wyq.project_springboot.utils.VerificationCodeUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.wyq.project_springboot.utils.RedisConstants.USERACCESS_TOKEN_KEY;
import static com.wyq.project_springboot.utils.RedisConstants.USERACCESS_TOKEN_TTL;

@RestController
@Validated
@RequestMapping("/userAccess")
public class UserAccessController {

    @Autowired
    private UserAccessService userAccessService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public Result login(@Pattern(regexp = "^\\d{11}$") String phoneNumber, @Pattern(regexp = "^\\S{6,16}$") String password, @Pattern(regexp = "^\\S{4}$") String verificationCode, HttpSession session) {
        //获取服务器生成的验证码
        String serverVerificationCode = (String) session.getAttribute("verificationCode");

        //判断客户端发送的验证码是否有误
        if (verificationCode.equalsIgnoreCase(serverVerificationCode)) {

            //根据手机号查询并获取用户
            User user = userAccessService.findUserByPhoneNumber(phoneNumber);

            if (user == null) {
                return Result.error("手机号码未注册");
            } else {

                //判断密码是否正确
                if (user.getPassword().equals(MD5Util.setPasswordToMD5(password))) {

                    //向客户端发送用户信息和token
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("id", user.getId());
                    claims.put("phoneNumber", user.getPhoneNumber());
                    String token = JwtUtil.genToken(claims);

                    //将token存入redis中
                    ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
                    stringStringValueOperations.set(USERACCESS_TOKEN_KEY + token,token,USERACCESS_TOKEN_TTL, TimeUnit.MINUTES);

                    Map resultList = new HashMap();
                    resultList.put("userInfo",user);
                    resultList.put("token",token);
                    return Result.success(resultList);
                } else {
                    return Result.error("密码错误");
                }
            }

        } else {
            return Result.error("验证码错误");
        }
    }

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{2,20}$") String accountName, @Pattern(regexp = "^\\d{11}$") String phoneNumber, @Pattern(regexp = "^\\S{6,16}$") String password, @Pattern(regexp = "^\\S{4}$") String verificationCode, HttpSession session) {
        //获取服务器生成的验证码
        String serverVerificationCode = (String) session.getAttribute("verificationCode");

        //判断客户端发送的验证码是否有误
        if (verificationCode.equalsIgnoreCase(serverVerificationCode)) {

            //根据手机号查询并获取用户
            User user = userAccessService.findUserByPhoneNumber(phoneNumber);

            if (user == null) {

                user = new User();
                user.setAccountName(accountName);
                user.setPhoneNumber(phoneNumber);
                user.setPassword(MD5Util.setPasswordToMD5(password));

                return userAccessService.register(user);
            } else {
                return Result.error("手机号码已存在");
            }

        } else {
            return Result.error("验证码错误");
        }

    }

    @GetMapping("/getVerificationCode")
    public Result getVerificationCode(HttpSession session) throws IOException {
        VerificationCodeUtil verificationCode = new VerificationCodeUtil();

        //获取验证码图片，方法内会生成验证码字符串，并赋值给对象属性
        BufferedImage image = verificationCode.getImage();
        //将验证码存入session中
        session.setAttribute("verificationCode", verificationCode.getCode());
        //将图片以base64的格式返回给前端
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        return Result.success("data:image/png;base64," + Base64.encodeBase64String(imageBytes));
    }
}
