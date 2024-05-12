package com.harmonylink.harmonylink.services.realtime;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.services.user.useractivity.UserWebSocketSessionService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInCallPairService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;


@Component
public class WebRTCService {

    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserInCallPairService userInCallPairService;


    @Autowired
    public WebRTCService (UserWebSocketSessionService userWebSocketSessionService, UserInCallPairService userInCallPairService) {
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userInCallPairService = userInCallPairService;
    }


    public void initiateConnection(UserProfile userProfile1) throws JSONException, IOException {
        WebSocketSession user1Session = this.userWebSocketSessionService.getWebSocketSession(userProfile1.getId());

        user1Session.sendMessage(new TextMessage(new JSONObject().put("type", "INITIATE_OFFER").toString()));
    }

    public void handleVideoOffer(WebSocketSession session, JSONObject offer) throws JSONException, IOException {
        String sdp = offer.getString("sdp");

        WebSocketSession peerSession = findPeerSession(session);

        if (peerSession != null) {
            JSONObject videoOffer = new JSONObject();

            videoOffer.put("type", "VIDEO_OFFER");
            videoOffer.put("sdp", sdp);

            peerSession.sendMessage(new TextMessage(videoOffer.toString()));
        }

    }

    public void handleVideoAnswer(WebSocketSession session, JSONObject answer) throws JSONException, IOException {
        String sdp = answer.getString("sdp");

        WebSocketSession peerSession = findPeerSession(session);

        if (peerSession != null) {
            JSONObject videoAnswer = new JSONObject();

            videoAnswer.put("type", "VIDEO_ANSWER");
            videoAnswer.put("sdp", sdp);

            peerSession.sendMessage(new TextMessage(videoAnswer.toString()));
        }
    }

    public void handleNewIceCandidate(WebSocketSession session, JSONObject candidate) throws JSONException, IOException {
        WebSocketSession peerSession = findPeerSession(session);

        if (peerSession != null) {
            JSONObject iceCandidate = new JSONObject();

            iceCandidate.put("type", "NEW_ICE_CANDIDATE");
            iceCandidate.put("candidate", candidate);

            peerSession.sendMessage(new TextMessage(iceCandidate.toString()));
        }
    }


    public WebSocketSession findPeerSession(WebSocketSession session) {
        String currentUserProfileId = (String) session.getAttributes().get("userProfileId");

        String peerUserProfileId = findPeerUserProfileId(currentUserProfileId).join();

        return this.userWebSocketSessionService.getWebSocketSession(peerUserProfileId);
    }

    @Async
    public CompletableFuture<String> findPeerUserProfileId(String currentUserProfileId) {
        String peerUserProfileId = this.userInCallPairService.getAllUserCallInPairData().stream()
                .filter(userCallPairData ->
                        userCallPairData.getUserProfile1().getId().equals(currentUserProfileId) ||
                                userCallPairData.getUserProfile2().getId().equals(currentUserProfileId))
                .findFirst()
                .map(userCallPairData ->
                        userCallPairData.getUserProfile1().getId().equals(currentUserProfileId) ?
                                userCallPairData.getUserProfile2().getId() :
                                userCallPairData.getUserProfile1().getId())
                .orElse(null);

        return CompletableFuture.completedFuture(peerUserProfileId);
    }

}
