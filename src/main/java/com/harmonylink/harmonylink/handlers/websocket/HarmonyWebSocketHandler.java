package com.harmonylink.harmonylink.handlers.websocket;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.UserTalkersHistoryService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInCallPairService;
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
    private final UserInCallPairService userInCallPairService;
    private final WebRTCService webRTCService;
    private final UserTalkersHistoryService userTalkersHistoryService;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;


    @Autowired
    public HarmonyWebSocketHandler(UserActivityStatusService userActivityStatusService, UserWebSocketSessionService userWebSocketSessionService, UserInSearchService userInSearchService, UserInCallPairService userInCallPairService, WebRTCService webRTCService, UserTalkersHistoryService userTalkersHistoryService, UserProfileRepository userProfileRepository, UserPreferencesFilterRepository userPreferencesFilterRepository) {
        this.userActivityStatusService = userActivityStatusService;
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userInSearchService = userInSearchService;
        this.userInCallPairService = userInCallPairService;
        this.webRTCService = webRTCService;
        this.userTalkersHistoryService = userTalkersHistoryService;
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

        if ("STOP_SEARCHING".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.ONLINE);

            this.userInSearchService.removeUserSearchData(userProfileId);
        }

        if ("STOP_WEBRTC_CONN".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userTalkersHistoryService.saveUserTalkersHistory(userProfileId);

            this.userInCallPairService.removeUserCallPairDataByUserProfileId(userProfileId);
        }

        if ("GET_TALKER_NICKNAME".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.webRTCService.getTalkerNickname(userProfileId, session);
        }

        if ("offer".equals(jsonMessage.getString("type"))) {
            this.webRTCService.handleVideoOffer(session, jsonMessage);
        }

        if ("answer".equals(jsonMessage.getString("type"))) {
            this.webRTCService.handleVideoAnswer(session, jsonMessage);
        }

        if ("candidate".equals(jsonMessage.getString("type"))) {
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
