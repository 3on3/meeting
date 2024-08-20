
package com.project.api.config;

import com.project.api.handler.ChatWebSocketHandler;
import com.project.api.handler.MainWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(chatHandler(), "/socket/Chat/**").setAllowedOriginPatterns("*");
        registry.addHandler(mainHandler(), "/socket/**").setAllowedOriginPatterns("*");
    }

    @Bean
    public WebSocketHandler chatHandler() {
        return new ChatWebSocketHandler(chatSessions());
    }

    @Bean
    public WebSocketHandler mainHandler() {
        return new MainWebSocketHandler(mainSessions());
    }

    @Bean
    public Map<String, WebSocketSession> chatSessions() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, WebSocketSession> mainSessions() {
        return new ConcurrentHashMap<>();
    }
}