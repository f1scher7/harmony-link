package com.harmonylink.harmonylink.controllers.navbarmenupages;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserProfileDataController {

    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;


    @Autowired
    public UserProfileDataController(UserProfileService userProfileService, UserProfileRepository userProfileRepository) {
        this.userProfileService = userProfileService;
        this.userProfileRepository = userProfileRepository;
    }


    @GetMapping("/user-profile-data")
    public String getUserProfileDataPage(Model model, Authentication authentication) {
        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);
        List<String> hobbies = this.userProfileService.getHobbies(userProfile.getHobbyIds());

        model.addAttribute("userProfile", userProfile);
        model.addAttribute("city", userProfile.getCity());
        model.addAttribute("sex", userProfile.getSex());
        model.addAttribute("age", userProfile.getAge());
        model.addAttribute("height", userProfile.getHeight());
        model.addAttribute("relationshipStatus", userProfile.getRelationshipStatus());
        model.addAttribute("hobbies", hobbies);
        model.addAttribute("study", userProfile.getFieldOfStudy());

        return "navbarMenuPages/userProfileData";
    }



}
