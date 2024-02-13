package com.wyq.project_springboot.interceptors;

import com.wyq.project_springboot.dto.user.UserDTO;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.service.UserAccessService;
import com.wyq.project_springboot.service.UserService;
import com.wyq.project_springboot.utils.JwtUtil;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 管理拦截器
 */
public class AdminInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserAccessService userAccessService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        String phoneNumber = (String) userMap.get("phoneNumber");

        User user = userAccessService.findUserByPhoneNumber(phoneNumber);

        if(user.getAccountType() == 1){
            return true;
        }else{
            response.setStatus(401);
            return false;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //该方法一定要调用！否则可能导致内存泄露！！！
        ThreadLocalUtil.remove();
    }
}