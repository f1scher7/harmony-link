package com.harmonylink.harmonylink.services.realtime;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.useractivity.UserWebSocketSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.harmonylink.harmonylink.constants.WebsocketConstants.SEND_MESSAGE_FOR_RECIPIENT_JSON;

@Service
public class ChatService {

    private final Logger MESSAGES_LOGGER = LoggerFactory.getLogger("Messages");

    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserProfileRepository userProfileRepository;


    @Autowired
    public ChatService(UserWebSocketSessionService userWebSocketSessionService, UserProfileRepository userProfileRepository) {
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userProfileRepository = userProfileRepository;
    }


    @Async
    public void sendMessToTalker(String mess, String to, String from) throws IOException {
        UserProfile userProfileSender = this.userProfileRepository.findById(from).orElse(null);
        UserProfile userProfileRecipient = this.userProfileRepository.findByNickname(to);

        if (userProfileSender != null) {
            MESSAGES_LOGGER.info(userProfileSender.getNickname() + " sended mess :" + mess + "; to " + userProfileRecipient.getNickname());

            WebSocketSession webSocketSession = this.userWebSocketSessionService.getWebSocketSession(userProfileRecipient.getId());

            if (webSocketSession != null) {
                synchronized (webSocketSession) {
                    webSocketSession.sendMessage(new TextMessage(String.format(SEND_MESSAGE_FOR_RECIPIENT_JSON, mess)));
                }
            }
        }
    }

}
