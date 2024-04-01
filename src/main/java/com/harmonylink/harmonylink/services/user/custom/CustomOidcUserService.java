package com.harmonylink.harmonylink.services.user.custom;

import com.harmonylink.harmonylink.models.token.OidcIdGoogleToken;
import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.token.OidcIdGoogleTokenRepository;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.services.user.useraccount.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Date;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final GoogleAuthService googleAuthService;
    private final UserAccountRepository userAccountRepository;
    private final OidcIdGoogleTokenRepository oidcIdTokenRepository;


    @Autowired
    public CustomOidcUserService(GoogleAuthService googleAuthService, UserAccountRepository userAccountRepository, OidcIdGoogleTokenRepository oidcIdGoogleTokenRepository) {
        this.googleAuthService = googleAuthService;
        this.userAccountRepository = userAccountRepository;
        this.oidcIdTokenRepository = oidcIdGoogleTokenRepository;
    }


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcIdToken oidcIdToken = userRequest.getIdToken();

        String googleId = (String) oidcIdToken.getClaims().get("sub");
        String displayName = (String) oidcIdToken.getClaims().get("name");
        String email = (String) oidcIdToken.getClaims().get("email");
        String profilePictureUrl = (String) oidcIdToken.getClaims().get("picture");

        System.out.println("name: " + displayName);
        System.out.println("picture: " + profilePictureUrl);

        String tokenId = oidcIdToken.getTokenValue();
        String issuer = (String) oidcIdToken.getClaims().get("iss");
        Date issuedAt = oidcIdToken.getIssuedAt() != null ? Date.from(oidcIdToken.getIssuedAt()) : null;
        Date expiresAt = oidcIdToken.getExpiresAt() != null ? Date.from(oidcIdToken.getExpiresAt()) : null;

        String ip;
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            HttpServletRequest request = sra.getRequest();
            ip = request.getRemoteAddr();
        } else {
            ip = null;
        }

        this.googleAuthService.saveOrUpdateUserAccount(new UserAccount(googleId, displayName, email, profilePictureUrl), ip);

        UserAccount userAccount = this.userAccountRepository.findByGoogleId(googleId);

        if (this.oidcIdTokenRepository.findByUserAccount(userAccount) == null) {
            this.oidcIdTokenRepository.save(new OidcIdGoogleToken(tokenId, issuer, userAccount, issuedAt, expiresAt));
        } else {
            OidcIdGoogleToken oidcIdGoogleToken = this.oidcIdTokenRepository.findByUserAccount(userAccount);
            oidcIdGoogleToken.setTokenId(tokenId);
            oidcIdGoogleToken.setIssuer(issuer);
            oidcIdGoogleToken.setIssuedAt(issuedAt);
            oidcIdGoogleToken.setExpiresAt(expiresAt);

            this.oidcIdTokenRepository.save(oidcIdGoogleToken);
        }

        return new DefaultOidcUser(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                userRequest.getIdToken(),
                "sub"
        );
    }

}