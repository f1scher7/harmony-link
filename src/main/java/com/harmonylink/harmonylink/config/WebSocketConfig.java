package com.harmonylink.harmonylink.config;

import com.harmonylink.harmonylink.handlers.websocket.handler.HarmonyWebSocketHandler;
import com.harmonylink.harmonylink.services.user.UserActivityStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UserActivityStatusService userActivityStatusService;


    @Autowired
    public WebSocketConfig(UserActivityStatusService userActivityStatusService) {
        this.userActivityStatusService = userActivityStatusService;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(harmonyWebSocket(), "/harmony-websocket");
    }

    @Bean
    public WebSocketHandler harmonyWebSocket() {
        return new HarmonyWebSocketHandler(this.userActivityStatusService);
    }

}
