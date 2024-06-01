package com.harmonylink.harmonylink.controllers.navbarmenupages;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserAccountSettingsController {

    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;


    @Autowired
    public UserAccountSettingsController(UserProfileService userProfileService, UserProfileRepository userProfileRepository) {
        this.userProfileService = userProfileService;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("account-settings")
    public String getAccountSettingsPage(Model model, Authentication authentication) {
        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);

        model.addAttribute("userProfileNickname", userProfile.getNickname());

        return "navbarMenuPages/accountSettings";
    }

}
