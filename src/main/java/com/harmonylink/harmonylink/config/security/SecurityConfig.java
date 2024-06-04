package com.harmonylink.harmonylink.config.security;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.custom.CustomOidcUserService;
import com.harmonylink.harmonylink.services.user.custom.CustomUserAccountDetailsService;
import com.harmonylink.harmonylink.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.authorization.AuthorizationDecision;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    private final SecurityUtil securityUtil;
    private final CustomUserAccountDetailsService userAccountDetailsService;
    private final CustomOidcUserService customOidcUserService;
    private final PasswordEncoder passwordEncoder;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;


    @Autowired
    public SecurityConfig(SecurityUtil securityUtil, CustomUserAccountDetailsService userAccountDetailsService, CustomOidcUserService customOidcUserService, PasswordEncoder passwordEncoder, ClientRegistrationRepository clientRegistrationRepository, UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository, OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.securityUtil = securityUtil;
        this.userAccountDetailsService = userAccountDetailsService;
        this.customOidcUserService = customOidcUserService;
        this.passwordEncoder = passwordEncoder;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userAccountDetailsService).passwordEncoder(this.passwordEncoder);
    }


    @Bean
    public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
        OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("accounts.google.com");
        idTokenDecoderFactory.setJwtValidatorFactory(clientRegistration -> withIssuer);
        return idTokenDecoderFactory;
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new com.harmonylink.harmonylink.handlers.security.AuthenticationSuccessHandler(this.userAccountRepository, this.userProfileRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthorizationManager<RequestAuthorizationContext> accessToSetProfile = (authenticationSupplier, context) -> {
                    boolean hasProfile = this.securityUtil.isUserProfileExists(authenticationSupplier.get());
                    Set<String> roles = AuthorityUtils.authorityListToSet(authenticationSupplier.get().getAuthorities());

                    boolean hasRequiredRole = roles.contains("ROLE_USER") || roles.contains("ROLE_ADMIN");

                    if (!hasProfile && hasRequiredRole) {
                        return new AuthorizationDecision(true);
                    } else {
                        return new AuthorizationDecision(false);
                    }
                };

        AuthorizationManager<RequestAuthorizationContext> accessToHome = (authenticationSupplier, context) -> {
                boolean hasProfile = this.securityUtil.isUserProfileExists(authenticationSupplier.get());
                Set<String> roles = AuthorityUtils.authorityListToSet(authenticationSupplier.get().getAuthorities());

                boolean hasRequiredRole = roles.contains("ROLE_USER") || roles.contains("ROLE_ADMIN");

                return new AuthorizationDecision(hasProfile && hasRequiredRole);
            };

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth","/auth/login", "/auth/registration", "/login/oauth2/code/google","/auth/activate-account", "/auth/forgot-password", "/auth/reset-password", "/auth/change-email-success").permitAll()
                        .requestMatchers("/auth/confirm-email").hasAuthority("ROLE_TEMP_USER")
                        .requestMatchers("/set-profile").access(accessToSetProfile)
                        .requestMatchers("/", "/terms-of-service", "/user-profile-data", "/talkers-history", "/account-settings").access(accessToHome)
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                            UserAccount userAccount = null;
                            if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
                                userAccount = SecurityConfig.this.userAccountRepository.findByGoogleId(oAuth2AuthenticationToken.getPrincipal().getAttribute("sub"));
                            } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
                                userAccount = SecurityConfig.this.userAccountRepository.findByLogin(usernamePasswordAuthenticationToken.getName());
                            }
                            if (userAccount != null && SecurityConfig.this.userProfileRepository.findByUserAccount(userAccount) == null) {
                                response.sendRedirect(request.getContextPath() + "/set-profile");
                            } else {
                                response.sendRedirect(request.getContextPath() + "/auth");
                            }
                        })
                )
                .formLogin(form -> form
                        .loginPage("/auth")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("login")
                        .failureUrl("/auth/login?error=true")
                        .successHandler(successHandler())
                        .permitAll()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/auth/login?error=true")
                        .successHandler(successHandler())
                        .permitAll()
                        .clientRegistrationRepository(this.clientRegistrationRepository)
                        .authorizedClientService(this.oAuth2AuthorizedClientService)
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(this.customOidcUserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }

}

