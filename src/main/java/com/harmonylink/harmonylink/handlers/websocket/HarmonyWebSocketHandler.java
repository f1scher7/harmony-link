package com.harmonylink.harmonylink.handlers.websocket;

import com.harmonylink.harmonylink.constants.WebsocketConstants;
import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.services.realtime.ChatService;
import com.harmonylink.harmonylink.services.realtime.WebSocketService;
import com.harmonylink.harmonylink.services.user.UserTalkersHistoryService;
import com.harmonylink.harmonylink.services.user.useractivity.*;
import com.harmonylink.harmonylink.services.realtime.WebRTCService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
public class HarmonyWebSocketHandler implements WebSocketHandler {

    private final UserActivityStatusService userActivityStatusService;
    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserTabsControlService userTabsControlService;
    private final UserInSearchService userInSearchService;
    private final UserInCallPairService userInCallPairService;
    private final WebSocketService webSocketService;
    private final WebRTCService webRTCService;
    private final ChatService chatService;
    private final UserTalkersHistoryService userTalkersHistoryService;


    @Autowired
    public HarmonyWebSocketHandler(UserActivityStatusService userActivityStatusService, UserWebSocketSessionService userWebSocketSessionService, UserTabsControlService userTabsControlService, UserInSearchService userInSearchService, UserInCallPairService userInCallPairService, WebSocketService webSocketService, WebRTCService webRTCService, ChatService chatService, UserTalkersHistoryService userTalkersHistoryService) {
        this.userActivityStatusService = userActivityStatusService;
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userTabsControlService = userTabsControlService;
        this.userInSearchService = userInSearchService;
        this.userInCallPairService = userInCallPairService;
        this.webSocketService = webSocketService;
        this.webRTCService = webRTCService;
        this.chatService = chatService;
        this.userTalkersHistoryService = userTalkersHistoryService;
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

            this.userTabsControlService.incrementTabsCounter(userProfileId);
            this.userWebSocketSessionService.addWebSocketSession(userProfileId, session);

            this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.ONLINE);
        }

        if ("HEARTBEAT_REQUEST".equals(jsonMessage.getString("type"))) {
            this.webSocketService.sendSynchronizedMessage(session, WebsocketConstants.HEARTBEAT_RESPONSE_JSON);
        }

        if ("IN_SEARCH".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userInSearchService.addUserSearchData(userProfileId);
        }

        if ("STOP_SEARCHING".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.ONLINE);

            this.userInSearchService.removeUserSearchData(userProfileId);
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

        if ("WEBRTC_CONN_ERROR".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userInCallPairService.removeUserCallPairDataByUserProfileId(userProfileId);
        }

        if ("GET_TALKER_NICKNAME".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.webRTCService.getTalkerNickname(userProfileId, session);
        }

        if ("SEND_TEXT_MESS_TO_SERVER".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("from");
            String talkerNickname = jsonMessage.getString("to");
            String mess = jsonMessage.getString("mess");

            this.chatService.sendMessToTalker(mess, talkerNickname, userProfileId);
        }

        if ("STOP_WEBRTC_CONN".equals(jsonMessage.getString("type"))) {
            String userProfileId = jsonMessage.getString("userProfileId");

            this.userTalkersHistoryService.saveUserTalkersHistory(userProfileId);

            this.userInCallPairService.removeUserCallPairDataByUserProfileId(userProfileId);
        }

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String userProfileId = this.userActivityStatusService.retrieveUserIdFromWebSocketSession(session);

        if (this.userTabsControlService.decrementTabsCounter(userProfileId)) {
            if (this.userInCallPairService.getUserProfileByUserProfileId(userProfileId) != null) {
                this.userInCallPairService.removeUserCallPairDataByUserProfileId(userProfileId);
            }

            this.userInSearchService.removeUserSearchData(userProfileId);
            this.userWebSocketSessionService.removeWebSocketSession(userProfileId);

            this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.OFFLINE);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
