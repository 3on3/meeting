
package  com.project.api.testChat;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.api.metting.dto.response.ChatMessageResponseDto;
import com.project.api.metting.entity.ChatMessage;
import com.project.api.metting.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.Map;


public class MyWebSocketHandler extends TextWebSocketHandler {


    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions;

    public MyWebSocketHandler(Map<String, WebSocketSession> sessions) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 추가된 설정
        this.sessions = sessions;
    }

    //최초 연결 시
    @OnOpen
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        final String sessionId = session.getId();
        final String enteredMessage = sessionId + "가";

        System.out.println("enteredMessage = " + enteredMessage);

        sessions.put(sessionId, session);

        String jsonMessage = objectMapper.writeValueAsString(enteredMessage);
        sendMessage(sessionId, new TextMessage(jsonMessage));
    }

    //양방향 데이터 통신할 떄 해당 메서드가 call 된다.
    @OnMessage
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //do something
        final String sessionId = session.getId();
        ChatMessage sendMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        System.out.println("message = " + message.getPayload());

        sessions.values().forEach((s) -> {

            if (!s.getId().equals(sessionId) && s.isOpen()) {
                try {
                    String jsonMessage = objectMapper.writeValueAsString(sendMessage);
                    s.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 자신이 보낼 메시지일 경우
                System.out.println("ㅣㅏㅣㅏㅣㅣㅏ");
            }
        });
    }

    //웹소켓 종료
    @OnClose
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        final String sessionId = session.getId();
        final String leaveMessage = sessionId + "님이 떠났습니다.";

        sessions.remove(sessionId); // 삭제

        //메시지 전송
        String jsonMessage = objectMapper.writeValueAsString(leaveMessage);
        sendMessage(sessionId, new TextMessage(jsonMessage));
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