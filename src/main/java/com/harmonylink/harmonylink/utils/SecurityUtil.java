package com.harmonylink.harmonylink.utils;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public SecurityUtil(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
    }


    public boolean isUserProfileExists(Authentication authentication) {
        UserAccount userAccount = null;

        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            userAccount = this.userAccountRepository.findByGoogleId(oAuth2AuthenticationToken.getPrincipal().getAttribute("sub"));
        } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            userAccount = this.userAccountRepository.findByLogin(usernamePasswordAuthenticationToken.getName());
        }

        return userAccount != null && this.userProfileRepository.findByUserAccount(userAccount) != null;
     }

}
