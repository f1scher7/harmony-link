package com.harmonylink.harmonylink.services.realtime;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.services.user.useractivity.UserWebSocketSessionService;
import com.harmonylink.harmonylink.services.user.useractivity.UserActivityStatusService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInSearchService;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.UserProfileDoesntExistException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HeartBeatService {

    private final UserActivityStatusService userActivityStatusService;
    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserInSearchService userInSearchService;
    private final ConcurrentHashMap<String, Long> userRequestTimes = new ConcurrentHashMap<>();


    @Autowired
    public HeartBeatService(UserActivityStatusService userActivityStatusService, UserWebSocketSessionService userWebSocketSessionService, UserInSearchService userInSearchService) {
        this.userActivityStatusService = userActivityStatusService;
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userInSearchService = userInSearchService;
    }


    @Async
    public void sendAsyncHeartRequest(WebSocketSession session) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(new JSONObject().put("type", "HEARTBEAT_REQUEST").toString()));

                userRequestTimes.put(this.userActivityStatusService.retrieveUserIdFromWebSocketSession(session), System.currentTimeMillis());
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Scheduled(fixedRate = 30000)
    public void sendHeartBeatRequest() {
        for (WebSocketSession webSocketSession: this.userWebSocketSessionService.getAllWebSocketSessions()) {
            sendAsyncHeartRequest(webSocketSession);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkResponses() {
        long currentTime = System.currentTimeMillis();
        userRequestTimes.forEach((userProfileId, requestTime) -> {

            if (currentTime - requestTime > 35000) {
                WebSocketSession session = this.userWebSocketSessionService.getWebSocketSession(userProfileId);

                if (session != null) {
                    try {
                        session.close();
                        this.userInSearchService.removeUserSearchData(userProfileId);
                        this.userWebSocketSessionService.removeWebSocketSession(userProfileId);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                userRequestTimes.remove(userProfileId);

                try {
                    this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.OFFLINE);
                } catch (UserProfileDoesntExistException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
