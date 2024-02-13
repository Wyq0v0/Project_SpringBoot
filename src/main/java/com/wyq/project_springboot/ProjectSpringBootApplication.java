package com.wyq.project_springboot;

import com.wyq.project_springboot.handler.ChatWebSocketHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan(basePackages = "com.wyq.project_springboot.mapper")//该注解让MyBatis扫描mapper接口
@EnableScheduling//开启定时任务支持
@SpringBootApplication
public class ProjectSpringBootApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ProjectSpringBootApplication.class, args);

        //为WebSocketHandler设置service
        ChatWebSocketHandler.setChatService(run);
    }

}
