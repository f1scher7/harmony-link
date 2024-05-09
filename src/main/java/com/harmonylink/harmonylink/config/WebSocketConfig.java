package com.harmonylink.harmonylink.config;

import com.harmonylink.harmonylink.handlers.websocket.HarmonyWebSocketHandler;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.useractivitystatus.UserActivityStatusService;
import com.harmonylink.harmonylink.services.user.useractivitystatus.UserInSearchService;
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
    private final UserInSearchService userInSearchService;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;


    @Autowired
    public WebSocketConfig(UserActivityStatusService userActivityStatusService, UserInSearchService userInSearchService, UserProfileRepository userProfileRepository, UserPreferencesFilterRepository userPreferencesFilterRepository) {
        this.userActivityStatusService = userActivityStatusService;
        this.userInSearchService = userInSearchService;
        this.userProfileRepository = userProfileRepository;
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
    }


    @Bean
    public WebSocketHandler harmonyWebSocket() {
        return new HarmonyWebSocketHandler(this.userActivityStatusService, this.userInSearchService, this.userProfileRepository, this.userPreferencesFilterRepository);
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
