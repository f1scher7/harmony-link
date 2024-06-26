package com.harmonylink.harmonylink.services.realtime;

import com.harmonylink.harmonylink.constants.WebRTCConstants;
import com.harmonylink.harmonylink.constants.WebsocketConstants;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.services.user.useractivity.UserWebSocketSessionService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInCallPairService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
public class WebRTCService {

    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserInCallPairService userInCallPairService;


    @Autowired
    public WebRTCService (UserWebSocketSessionService userWebSocketSessionService, UserInCallPairService userInCallPairService) {
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userInCallPairService = userInCallPairService;
    }


    @Async
    public void initiateConnection(UserProfile userProfile1) throws JSONException, IOException {
        WebSocketSession user1Session = this.userWebSocketSessionService.getWebSocketSession(userProfile1.getId());

        if (user1Session != null) {
            synchronized (user1Session) {
                user1Session.sendMessage(new TextMessage(WebRTCConstants.INITIATE_OFFER_JSON));
            }
        }
    }

    @Async
    public void handleVideoOffer(WebSocketSession session, JSONObject offer) throws JSONException, IOException {
        String sdp = offer.getString("sdp");

        WebSocketSession peerSession = findPeerSession(session);

        if (peerSession != null) {
            JSONObject videoOffer = new JSONObject();

            videoOffer.put("type", "offer");
            videoOffer.put("sdp", sdp);

            synchronized (peerSession) {
                peerSession.sendMessage(new TextMessage(videoOffer.toString()));
            }
        }

    }

    @Async
    public void handleVideoAnswer(WebSocketSession session, JSONObject answer) throws JSONException, IOException {
        String sdp = answer.getString("sdp");

        WebSocketSession peerSession = findPeerSession(session);

        if (peerSession != null) {
            JSONObject videoAnswer = new JSONObject();

            videoAnswer.put("type", "answer");
            videoAnswer.put("sdp", sdp);

            synchronized (peerSession) {
                peerSession.sendMessage(new TextMessage(videoAnswer.toString()));
            }
        }
    }

    @Async
    public void handleNewIceCandidate(WebSocketSession session, JSONObject candidate) throws IOException, JSONException {
        WebSocketSession peerSession = findPeerSession(session);

        if (peerSession != null) {
            JSONObject iceCandidate = new JSONObject();

            iceCandidate.put("type", "candidate");
            iceCandidate.put("candidate", candidate);

            synchronized (peerSession) {
                peerSession.sendMessage(new TextMessage(iceCandidate.toString()));
            }
        }
    }


    @Async
    public void getTalkerNickname(String userProfileId, WebSocketSession session) throws IOException {
        String nickname = this.userInCallPairService.getUserProfileByUserProfileId(userProfileId).getNickname();
        String talkerNickname = nickname != null ? nickname : "error";

        WebSocketSession talkerSession = findPeerSession(session);
        if (talkerSession != null) {
            synchronized (talkerSession) {
                talkerSession.sendMessage(new TextMessage(String.format(WebsocketConstants.TALKER_NICKNAME_JSON, talkerNickname)));
            }
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
