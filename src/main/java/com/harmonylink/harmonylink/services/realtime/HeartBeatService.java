package com.harmonylink.harmonylink.services.realtime;

import com.harmonylink.harmonylink.constants.WebsocketConstants;
import com.harmonylink.harmonylink.services.user.useractivity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
public class HeartBeatService {

    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserInSearchService userInSearchService;
    private final UserInCallPairService userInCallPairService;


    @Autowired
    public HeartBeatService(UserWebSocketSessionService userWebSocketSessionService, UserInSearchService userInSearchService, UserInCallPairService userInCallPairService) {
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userInSearchService = userInSearchService;
        this.userInCallPairService = userInCallPairService;
    }


    @Async
    public void sendAsyncHeartRequest(WebSocketSession session) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(WebsocketConstants.HEARTBEAT_REQUEST_JSON));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Async
    @Scheduled(fixedDelay = 5000)
    public void sendHeartBeatRequestToAllSessions() {
        this.userWebSocketSessionService.getAllWebSocketSessions().stream()
                .filter(WebSocketSession::isOpen)
                .forEach(this::sendAsyncHeartRequest);
    }

    public void displaySessionInfo() {
        System.out.println("SESSION");
        System.out.println(this.userWebSocketSessionService.displayWebSocketSessions());
        System.out.println("============================");

        System.out.println("IN_SEARCH");
        System.out.println(this.userInSearchService.displayInSearchUsers());
        System.out.println("============================");

        System.out.println("IN_CALL");
        System.out.println(this.userInCallPairService.displayInSearchUsers());
        System.out.println("============================");
    }

}
