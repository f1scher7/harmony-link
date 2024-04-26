package com.harmonylink.harmonylink.controllers;

import com.harmonylink.harmonylink.enums.UserActivityStatus;
import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class HomeController {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;


    @Autowired
    public HomeController(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
    }


    @GetMapping("/users-activity-status")
    @ResponseBody
    public List<Long> getUsersActivityStatus() {
        List<Long> usersActivityStatus = new ArrayList<>();
        usersActivityStatus.add(this.userProfileRepository.countOnline());
        usersActivityStatus.add(this.userProfileRepository.countByInSearchStatus());
        usersActivityStatus.add(this.userProfileRepository.countByInCallStatus());

        return usersActivityStatus;
    }

    @PostMapping("/report-user-offline")
    @ResponseBody
    public void reportUserStatusOffline(@RequestBody Map<String, String> jsonBody) {
        String userProfileId = jsonBody.get("userProfileId");

        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId);
        UserProfile userProfile = null;

        if (userProfileOptional.isPresent()) {
            userProfile = userProfileOptional.get();
            userProfile.setActivityStatus(UserActivityStatus.OFFLINE);
            this.userProfileRepository.save(userProfile);
        }
    }


    @GetMapping
    public String showHomePage(Model model, Authentication authentication) {
        UserAccount userAccount = null;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");
            userAccount = this.userAccountRepository.findByEmail(email);
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            String username = authentication.getName();
            userAccount = this.userAccountRepository.findByLogin(username);
        }

        UserProfile userProfile = this.userProfileRepository.findByUserAccount(userAccount);
        userProfile.setActivityStatus(UserActivityStatus.ONLINE);
        this.userProfileRepository.save(userProfile);

        model.addAttribute("userProfile", userProfile);

        return "home";
    }

}
