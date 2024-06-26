package com.harmonylink.harmonylink.listeners.login;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        if (!(event.getAuthentication() instanceof OAuth2AuthenticationToken)) {

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

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            request.getSession().setAttribute("showHarmonyInfoModal", "true");

            USER_LOGIN_LOGGER.info("User " + login + " logged in. IP: " + ip);
        }
    }

}
