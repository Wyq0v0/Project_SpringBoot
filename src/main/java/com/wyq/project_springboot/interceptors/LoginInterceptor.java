package com.wyq.project_springboot.interceptors;

import com.wyq.project_springboot.utils.JwtUtil;
import com.wyq.project_springboot.utils.RedisConstants;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.wyq.project_springboot.utils.RedisConstants.USERACCESS_TOKEN_KEY;
import static com.wyq.project_springboot.utils.RedisConstants.USERACCESS_TOKEN_TTL;

/**
 * 登录校验拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //验证客户端传来的token
        String token = request.getHeader("Authorization");

        try {

            //从redis中尝试获取该token
            ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
            String redisToken = stringStringValueOperations.get(USERACCESS_TOKEN_KEY + token);

            //判断redis中是否有该token
            if(redisToken == null){
                throw new RuntimeException();
            }

            //刷新token有效期
            stringRedisTemplate.expire(USERACCESS_TOKEN_KEY + token, USERACCESS_TOKEN_TTL, TimeUnit.MINUTES);

            //解析claims后，丢进threadlocal中供后续的controller等使用
            Map<String,Object> claims = JwtUtil.parseToken(token);
            ThreadLocalUtil.set(claims);
            return true;
        } catch (Exception e) {
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
