package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.services.user.useractivitystatus.UserInCallPairService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WebRTCService {

    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserInCallPairService userInCallPairService;


    @Autowired
    public WebRTCService (UserWebSocketSessionService userWebSocketSessionService, UserInCallPairService userInCallPairService) {
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userInCallPairService = userInCallPairService;
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
        ExecutorService executor = Executors.newFixedThreadPool(10);

        return CompletableFuture.supplyAsync(() ->
                this.userInCallPairService.getAllUserCallInPairData().stream()
                        .filter(userCallPairData ->
                                userCallPairData.getUserProfile1().getId().equals(currentUserProfileId) ||
                                userCallPairData.getUserProfile2().getId().equals(currentUserProfileId))
                        .findFirst()
                        .map(userCallPairData ->
                              userCallPairData.getUserProfile1().getId().equals(currentUserProfileId) ?
                              userCallPairData.getUserProfile2().getId() :
                              userCallPairData.getUserProfile1().getId())
                        .orElse(null),
                executor);
    }

}
