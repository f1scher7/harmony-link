package com.harmonylink.harmonylink.config;

import com.harmonylink.harmonylink.handlers.websocket.HarmonyWebSocketHandler;
import com.harmonylink.harmonylink.services.realtime.WebSocketService;
import com.harmonylink.harmonylink.services.user.UserTalkersHistoryService;
import com.harmonylink.harmonylink.services.user.useractivity.*;
import com.harmonylink.harmonylink.services.realtime.WebRTCService;
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
    private final UserTabsControlService userTabsControlService;
    private final UserInSearchService userInSearchService;
    private final UserInCallPairService userInCallPairService;
    private final WebSocketService webSocketService;
    private final WebRTCService webRTCService;
    private final UserTalkersHistoryService userTalkersHistoryService;


    @Autowired
    public WebSocketConfig(UserActivityStatusService userActivityStatusService, UserWebSocketSessionService userWebSocketSessionService, UserTabsControlService userTabsControlService, UserInSearchService userInSearchService, UserInCallPairService userInCallPairService, WebSocketService webSocketService, WebRTCService webRTCService, UserTalkersHistoryService userTalkersHistoryService) {
        this.userActivityStatusService = userActivityStatusService;
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userTabsControlService = userTabsControlService;
        this.userInSearchService = userInSearchService;
        this.userInCallPairService = userInCallPairService;
        this.webSocketService = webSocketService;
        this.webRTCService = webRTCService;
        this.userTalkersHistoryService = userTalkersHistoryService;
    }


    @Bean
    public WebSocketHandler harmonyWebSocket() {
        return new HarmonyWebSocketHandler(
                this.userActivityStatusService,
                this.userWebSocketSessionService,
                this.userTabsControlService,
                this.userInSearchService,
                this.userInCallPairService,
                this.webSocketService,
                this.webRTCService,
                this.userTalkersHistoryService
        );
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
