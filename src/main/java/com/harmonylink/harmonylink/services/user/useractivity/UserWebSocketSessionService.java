package com.harmonylink.harmonylink.services.user.useractivity;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserWebSocketSessionService {

    private final ConcurrentHashMap<String, WebSocketSession> userWebSocketSessions = new ConcurrentHashMap<>();


    public void addWebSocketSession(String userProfileId, WebSocketSession webSocketSession) {
        if (userProfileId != null && webSocketSession != null) {
            userWebSocketSessions.put(userProfileId, webSocketSession);
        }
    }


    public WebSocketSession getWebSocketSession(String userProfileId) {
        if (userProfileId != null) {
            return userWebSocketSessions.get(userProfileId);
        }

        return null;
    }

    public List<WebSocketSession> getAllWebSocketSessions() {
        return new ArrayList<>(userWebSocketSessions.values());
    }

    public long getNumberOfSessions() {
        return userWebSocketSessions.size();
    }


    public void removeWebSocketSession(String userProfileId) {
        if (userProfileId != null) {
            userWebSocketSessions.remove(userProfileId);
        }
    }

    public void removeAllWebSocketSessions() {
        userWebSocketSessions.clear();
    }


    public String displayWebSocketSessions() {
        StringBuilder builder = new StringBuilder();
        userWebSocketSessions.forEach((key, value) -> builder
                .append("Id: ").append(key)
                .append("; WebSocketSession: ").append(value.toString())
                .append("\n"));
        return builder.toString();
    }

}
