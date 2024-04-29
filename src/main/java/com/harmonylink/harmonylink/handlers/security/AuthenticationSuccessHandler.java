package com.harmonylink.harmonylink.handlers.security;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public AuthenticationSuccessHandler(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }

        if (isTempUser(authentication)) {
            response.sendRedirect(request.getContextPath() + "/auth/confirm-email");
        } else {
            UserAccount userAccount = null;

            if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
                userAccount = this.userAccountRepository.findByGoogleId(oAuth2AuthenticationToken.getPrincipal().getAttribute("sub"));
            } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
                userAccount = this.userAccountRepository.findByLogin(usernamePasswordAuthenticationToken.getName());
            }

            if (this.userProfileRepository.findByUserAccount(userAccount) == null) {
                response.sendRedirect(request.getContextPath() + "/set-profile");
            } else if (this.userProfileRepository.findByUserAccount(userAccount) != null) {
                super.setDefaultTargetUrl("/");
                super.onAuthenticationSuccess(request, response, authentication);
            } else {
                response.sendRedirect(request.getContextPath() + "/auth/login?error=true");
            }
        }
    }

    private boolean isTempUser(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_TEMP_USER"));
    }

}
