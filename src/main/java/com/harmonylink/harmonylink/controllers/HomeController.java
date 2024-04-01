package com.harmonylink.harmonylink.controllers;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final UserAccountRepository userAccountRepository;

    @Autowired
    public HomeController(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public String showHomePage(Model model, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");
            model.addAttribute("username", email);

            UserAccount userAccount = this.userAccountRepository.findByEmail(email);

            model.addAttribute("googleId", userAccount.getGoogleId());
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            String username = authentication.getName();

            model.addAttribute("username", username);

            UserAccount userAccount = this.userAccountRepository.findByLogin(username);
            List<String> ips = userAccount.getIpAddresses();

            model.addAttribute("email", userAccount.getEmail());
            model.addAttribute("password", userAccount.getPassword());
            model.addAttribute("sex", userAccount.getSex());
            model.addAttribute("ip", ips.get(0));
        }

        return "home";
    }

}
