
package com.project.api.handler;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.api.metting.dto.request.ChatMessageRequestDto;
import com.project.api.metting.dto.response.ChatWebSocketResponseDto;
import com.project.api.metting.dto.response.LoginResponseDto;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ChatWebSocketHandler extends TextWebSocketHandler {


    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions;
    private final Map<String, String> users = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(Map<String, WebSocketSession> sessions) {
        this.objectMapper = new ObjectMapper();
        // localDateTime 을 제대로 받아오기 위한 코드
        this.objectMapper.registerModule(new JavaTimeModule());
        // withdraw 오류
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 추가된 설정
        this.sessions = sessions;
    }

    //최초 연결 시
    @OnOpen
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        final String sessionId = session.getId();

        if(!sessions.containsKey(sessionId)) {
            sessions.put(sessionId, session);
        }

    }

    //양방향 데이터 통신할 떄 해당 메서드가 call 된다.
    @OnMessage
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //do something

        System.out.println("session = " + message.getPayload());

        ChatWebSocketResponseDto data = objectMapper.readValue(message.getPayload(), ChatWebSocketResponseDto.class);

        System.out.println("data = " + data);
        System.out.println("data = " + data);

        if(data.getType().equals("message")) {
            String chatroomId = data.getChatroomId();

            for (Map.Entry<String, String> entry : users.entrySet()) {
                if (chatroomId.equals(entry.getValue())) {
                    sessions.values().forEach((s) -> {
                        if(s.getId().equals(entry.getKey())) {
                            try {
                                String jsonMessage = objectMapper.writeValueAsString(data.getMessage());
                                s.sendMessage(new TextMessage(jsonMessage));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        } else if(data.getType().equals("enter")) {
            users.put(session.getId(), data.getChatroomId());
        }
    }

    //웹소켓 종료
    @OnClose
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        final String sessionId = session.getId();
        try {
            sessions.remove(sessionId); // 삭제
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //통신 에러 발생 시
    @OnError
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("session = " + session);
    }

    private void sendMessage(String sessionId, WebSocketMessage<?> message) {
        sessions.values().forEach(s -> {
            if(!s.getId().equals(sessionId) && s.isOpen()) {
                try {
                    s.sendMessage(message);
                } catch (IOException e) {}
            }
        });
    }



}