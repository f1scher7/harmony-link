package com.harmonylink.harmonylink.listeners.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoginFailureListener {

    private final Logger USER_LOGIN_LOGGER = LoggerFactory.getLogger("UserLogin");

    @EventListener
    public void onLoginFailure(AuthenticationFailureBadCredentialsEvent event) {
        String login = event.getAuthentication().getName();
        USER_LOGIN_LOGGER.info("Login attempt for user " + login + " failed at " + LocalDateTime.now());
    }

}
