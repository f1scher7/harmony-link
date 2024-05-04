package com.harmonylink.harmonylink.handlers.websocket;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.services.user.UserActivityStatusService;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.UserProfileDoesntExistException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HarmonyWebSocketHandler implements WebSocketHandler, HeartBeatChecker {

    private final UserActivityStatusService userActivityStatusService;
    private final Map<String, WebSocketSession> webSocketSessionMap;
    private final Map<String, Long> userRequestTimes;

    @Autowired
    public HarmonyWebSocketHandler(UserActivityStatusService userActivityStatusService) {
        this.userActivityStatusService = userActivityStatusService;
        this.webSocketSessionMap = new ConcurrentHashMap<>();
        this.userRequestTimes = new ConcurrentHashMap<>();
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        JSONObject jsonMessage = new JSONObject(payload);

        if ("USER_PROFILE_ID".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");
            session.getAttributes().put("userProfileId", userProfileId);

            this.webSocketSessionMap.put(userProfileId, session);
            this.userActivityStatusService.updateUserActivityStatus(userProfileId, UserActivityStatusEnum.ONLINE);
        }
        if ("HEART_BEAT_REQUEST".equals(jsonMessage.getString("type"))) {
            session.sendMessage(new TextMessage("HEARTBEAT_RESPONSE"));
            System.out.println("git");
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userProfileId = this.userActivityStatusService.retrieveUserIdFromWebSocketSession(session);

        this.webSocketSessionMap.remove(userProfileId);
        this.userActivityStatusService.updateUserActivityStatus(userProfileId, UserActivityStatusEnum.OFFLINE);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    @Async
    public void sendAsyncHeartRequest(WebSocketSession session) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage("HEARTBEAT_REQUEST"));

                this.userRequestTimes.put(this.userActivityStatusService.retrieveUserIdFromWebSocketSession(session), System.currentTimeMillis());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @Scheduled(fixedRate = 30000)
    public void sendHeartBeatRequest() {
        for (WebSocketSession webSocketSession: webSocketSessionMap.values()) {
            sendAsyncHeartRequest(webSocketSession);
        }
    }

    @Override
    @Scheduled(fixedRate = 30000)
    public void checkResponses() {
        long currentTime = System.currentTimeMillis();
        this.userRequestTimes.forEach((userProfileId, requestTime) -> {

            if (currentTime - requestTime > 35000) {
                WebSocketSession session = this.webSocketSessionMap.get(userProfileId);

                if (session != null) {
                    try {
                        session.close();
                        this.webSocketSessionMap.remove(userProfileId);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                this.userRequestTimes.remove(userProfileId);

                try {
                    this.userActivityStatusService.updateUserActivityStatus(userProfileId, UserActivityStatusEnum.OFFLINE);
                } catch (UserProfileDoesntExistException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
