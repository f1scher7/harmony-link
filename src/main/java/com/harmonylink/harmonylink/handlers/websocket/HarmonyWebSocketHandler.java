package com.harmonylink.harmonylink.handlers.websocket;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.useractivity.UserWebSocketSessionService;
import com.harmonylink.harmonylink.services.realtime.WebRTCService;
import com.harmonylink.harmonylink.services.user.useractivity.UserActivityStatusService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInSearchService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Optional;

@Component
public class HarmonyWebSocketHandler implements WebSocketHandler {

    private final UserActivityStatusService userActivityStatusService;
    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserInSearchService userInSearchService;
    private final WebRTCService webRTCService;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;


    @Autowired
    public HarmonyWebSocketHandler(UserActivityStatusService userActivityStatusService, UserWebSocketSessionService userWebSocketSessionService, UserInSearchService userInSearchService, WebRTCService webRTCService, UserProfileRepository userProfileRepository, UserPreferencesFilterRepository userPreferencesFilterRepository) {
        this.userInSearchService = userInSearchService;
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userActivityStatusService = userActivityStatusService;
        this.webRTCService = webRTCService;
        this.userProfileRepository = userProfileRepository;
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
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

            this.userWebSocketSessionService.addWebSocketSession(userProfileId, session);
            this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.ONLINE);
        }

        if ("HEARTBEAT_REQUEST".equals(jsonMessage.getString("type"))) {
            session.sendMessage(new TextMessage(new JSONObject().put("type", "HEARTBEAT_RESPONSE").toString()));
        }

        if ("IN_SEARCH".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.IN_SEARCH);

            Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId);
            UserProfile userProfile = null;
            if (userProfileOptional.isPresent()) {
                userProfile = userProfileOptional.get();
            }

            UserPreferencesFilter userPreferencesFilter = this.userPreferencesFilterRepository.findByUserProfile(userProfile);
            this.userInSearchService.addUserSearchData(userProfile, userPreferencesFilter);
        }

        if ("STOP_ACTIVITY".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.ONLINE);

            this.userInSearchService.removeUserSearchData(userProfileId);
        }

        if ("VIDEO_OFFER".equals(jsonMessage.getString("type"))) {
            this.webRTCService.handleVideoOffer(session, jsonMessage);
        }

        if ("VIDEO_ANSWER".equals(jsonMessage.getString("type"))) {
            this.webRTCService.handleVideoAnswer(session, jsonMessage);
        }

        if ("NEW_ICE_CANDIDATE".equals(jsonMessage.getString("type"))) {
            this.webRTCService.handleNewIceCandidate(session, jsonMessage);
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userProfileId = this.userActivityStatusService.retrieveUserIdFromWebSocketSession(session);

        this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.OFFLINE);

        this.userInSearchService.removeUserSearchData(userProfileId);
        this.userWebSocketSessionService.removeWebSocketSession(userProfileId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
