//package com.project.api.testChat.config;
//
//import com.project.api.testChat.MyWebSocketHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//
//        registry.addHandler(myHandler(), "/testChat/**").setAllowedOriginPatterns("*");
//    }
//
//    @Bean
//    public WebSocketHandler myHandler() {
//        return new MyWebSocketHandler();
//    }
//}