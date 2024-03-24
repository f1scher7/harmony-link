package com.harmonylink.harmonylink.config;

import com.harmonylink.harmonylink.services.user.CustomUserAccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserAccountDetailsService userAccountDetailsService;

    @Autowired
    public SecurityConfig(CustomUserAccountDetailsService userAccountDetailsService) {
        this.userAccountDetailsService = userAccountDetailsService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/","/register","/login").permitAll() // Strony dostępne bez autoryzacji
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/") // Tutaj ustawiasz ścieżkę do Twojej strony logowania
                        .defaultSuccessUrl("/dashboard", true) // Tutaj ustawiasz stronę, na którą użytkownik zostanie przekierowany po pomyślnym zalogowaniu
                        .permitAll()
                );
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userAccountDetailsService)
                .passwordEncoder(passwordEncoder());
    }

}

