package com.harmonylink.harmonylink.config;

import com.harmonylink.harmonylink.handlers.websocket.HarmonyWebSocketHandler;
import com.harmonylink.harmonylink.services.user.UserTalkersHistoryService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInCallPairService;
import com.harmonylink.harmonylink.services.user.useractivity.UserWebSocketSessionService;
import com.harmonylink.harmonylink.services.realtime.WebRTCService;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.useractivity.UserActivityStatusService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private final UserActivityStatusService userActivityStatusService;
    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserInSearchService userInSearchService;
    private final UserInCallPairService userInCallPairService;
    private final WebRTCService webRTCService;
    private final UserTalkersHistoryService userTalkersHistoryService;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;


    @Autowired
    public WebSocketConfig(UserActivityStatusService userActivityStatusService, UserWebSocketSessionService userWebSocketSessionService, UserInSearchService userInSearchService, UserInCallPairService userInCallPairService, WebRTCService webRTCService, UserTalkersHistoryService userTalkersHistoryService, UserProfileRepository userProfileRepository, UserPreferencesFilterRepository userPreferencesFilterRepository) {
        this.userActivityStatusService = userActivityStatusService;
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userInSearchService = userInSearchService;
        this.userInCallPairService = userInCallPairService;
        this.webRTCService = webRTCService;
        this.userTalkersHistoryService = userTalkersHistoryService;
        this.userProfileRepository = userProfileRepository;
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
    }


    @Bean
    public WebSocketHandler harmonyWebSocket() {
        return new HarmonyWebSocketHandler(this.userActivityStatusService, this.userWebSocketSessionService, this.userInSearchService, this.userInCallPairService, this.webRTCService, this.userTalkersHistoryService, this.userProfileRepository, this.userPreferencesFilterRepository);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(harmonyWebSocket(), "/harmony-websocket-handler");
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker("/topic");
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint("/chat-connection").withSockJS();
    }

}
