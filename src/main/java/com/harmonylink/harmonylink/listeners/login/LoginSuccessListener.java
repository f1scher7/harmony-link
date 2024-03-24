package com.harmonylink.harmonylink.listeners.login;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LoginSuccessListener {

    private final Logger USER_LOGIN_LOGGER = LoggerFactory.getLogger("UserLogin");

    private final UserAccountRepository userAccountRepository;


    @Autowired
    public LoginSuccessListener(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }


    @EventListener
    public void onLoginSuccess(InteractiveAuthenticationSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();

        String login = userDetails.getUsername();
        String ip = ((WebAuthenticationDetails) event.getAuthentication().getDetails()).getRemoteAddress();

        UserAccount userAccount = this.userAccountRepository.findByLogin(login);

        if (userAccount == null) {
            userAccount = this.userAccountRepository.findByEmail(login);
        }

        List<String> ips = userAccount.getIpAddresses();

        if (!ips.contains(ip)) {
            userAccount.addIpAddress(ip);
            this.userAccountRepository.save(userAccount);
        }

        USER_LOGIN_LOGGER.info("User " + login + " logged in at " + LocalDateTime.now());
    }

}
