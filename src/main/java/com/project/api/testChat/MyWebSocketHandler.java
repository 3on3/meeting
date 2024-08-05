package  com.project.api.testChat;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.api.auth.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MyWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(MyWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    //최초 연결 시
    @OnOpen
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        final String sessionId = session.getId();
        final String enteredMessage = sessionId + "qweqweqwe";

        sessions.put(sessionId, session);

        String jsonMessage = objectMapper.writeValueAsString(enteredMessage);
        sendMessage(sessionId, new TextMessage(jsonMessage));
    }

    //양방향 데이터 통신할 떄 해당 메서드가 call 된다.
    @OnMessage
    protected void handleTextMessage(WebSocketSession session, TextMessage message, @AuthenticationPrincipal TokenProvider.TokenUserInfo tokenUserInfo) throws Exception {
        //do something
        final String sessionId = session.getId();

        TestMessage testMessage = objectMapper.readValue(message.getPayload(), TestMessage.class);
        testMessage.setUserName(sessionId);

        sessions.values().forEach((s) -> {

            if (!s.getId().equals(sessionId) && s.isOpen()) {
                try {
                    testMessage.setAuth("otherUser");
                    String jsonMessage = objectMapper.writeValueAsString(testMessage);
                    s.sendMessage(new TextMessage(jsonMessage));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 자신이 보낼 메시지일 경우
                System.out.println("gkgkgkgkgkgdjskafjsdlfjasldfjsadlkf");
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