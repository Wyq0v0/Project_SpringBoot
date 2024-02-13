package com.wyq.project_springboot.config;

import com.wyq.project_springboot.interceptors.AdminInterceptor;
import com.wyq.project_springboot.interceptors.LoginInterceptor;
import com.wyq.project_springboot.utils.ImageUploadConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.wyq.project_springboot.utils.ImageUploadConstant.IMAGE_UPLOAD_PREFIX;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor getLoginInterceptor(){
        return new LoginInterceptor();
    }

    @Bean
    public AdminInterceptor getAdminInterceptor(){
        return new AdminInterceptor();
    }

    /**
     * 注册登录验证拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/userAccess/login","/userAccess/register","/userAccess/getVerificationCode","/image/**",
                        "/admin/adminAccess/login","/admin/adminAccess/getVerificationCode","/alipay/**");

        registry.addInterceptor(getAdminInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/adminAccess/login","/admin/adminAccess/getVerificationCode");
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/image/**").addResourceLocations("file:" + IMAGE_UPLOAD_PREFIX);
    }

}
