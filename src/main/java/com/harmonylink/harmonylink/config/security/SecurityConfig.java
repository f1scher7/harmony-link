package com.harmonylink.harmonylink.config.security;

import com.harmonylink.harmonylink.services.user.custom.CustomOidcUserService;
import com.harmonylink.harmonylink.services.user.custom.CustomUserAccountDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    private final CustomUserAccountDetailsService userAccountDetailsService;
    private final CustomOidcUserService customOidcUserService;
    private final PasswordEncoder passwordEncoder;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;


    @Autowired
    public SecurityConfig(CustomUserAccountDetailsService userAccountDetailsService, CustomOidcUserService customOidcUserService, PasswordEncoder passwordEncoder, ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.userAccountDetailsService = userAccountDetailsService;
        this.customOidcUserService = customOidcUserService;
        this.passwordEncoder = passwordEncoder;
        this.clientRegistrationRepository = clientRegistrationRepository;
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
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                if (response.isCommitted()) {
                    return;
                }
                if (isAutoLogin(authentication)) {
                    response.sendRedirect(request.getContextPath() + "/auth/confirm-email");
                } else {
                    super.setDefaultTargetUrl("/");
                    super.onAuthenticationSuccess(request, response, authentication);
                }
            }

            private boolean isAutoLogin(Authentication authentication) {
                return authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_TEMP_USER"));
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth","/auth/login", "/auth/registration", "/login/oauth2/code/google","/auth/activate-account", "/auth/forgot-password", "/auth/reset-password").permitAll()
                        .requestMatchers("/auth/confirm-email").hasAuthority("ROLE_TEMP_USER")
                        .requestMatchers("/").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/auth");
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
                        .permitAll()
                        .clientRegistrationRepository(this.clientRegistrationRepository)
                        .authorizedClientService(this.oAuth2AuthorizedClientService)
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(this.customOidcUserService)
                        )
                );
        return http.build();
    }

}

