package com.harmonylink.harmonylink.handlers.websocket;

import org.springframework.web.socket.WebSocketSession;

public interface HeartBeatChecker {

    void sendAsyncHeartRequest(WebSocketSession session);
    void sendHeartBeatRequest();
    void checkResponses();

}
