package com.wyq.project_springboot.config;

import com.wyq.project_springboot.handler.ChatWebSocketHandler;
import com.wyq.project_springboot.interceptors.WebSocketInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/chat")
                .addInterceptors(webSocketInterceptor())
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler chatWebSocketHandler(){
        return new ChatWebSocketHandler();
    }

    @Bean
    public WebSocketInterceptor webSocketInterceptor(){
        return new WebSocketInterceptor();
    }
}
