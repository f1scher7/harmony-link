package com.harmonylink.harmonylink.controllers.navbarmenupages;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.InvalidUserHobbiesExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UserProfileDataController {

    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;
    private final UserAccountRepository userAccountRepository;


    @Autowired
    public UserProfileDataController(UserProfileService userProfileService, UserProfileRepository userProfileRepository, UserAccountRepository userAccountRepository) {
        this.userProfileService = userProfileService;
        this.userProfileRepository = userProfileRepository;
        this.userAccountRepository = userAccountRepository;
    }


    @GetMapping("/user-profile-data")
    public String getUserProfileDataPage(Model model, @ModelAttribute("displaySuccessModal") String displaySuccessModal, Authentication authentication) {
        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);
        List<String> hobbies = this.userProfileService.getHobbies(userProfile.getHobbyIds());

        model.addAttribute("userProfile", userProfile);
        model.addAttribute("sex", userProfile.getSex());
        model.addAttribute("relationshipStatus", userProfile.getRelationshipStatus());
        model.addAttribute("hobbies", hobbies);

        if (displaySuccessModal.equals("1")) {
            model.addAttribute("displaySuccessModal", "1");
        }

        return "navbarMenuPages/userProfileData";
    }

    @PostMapping("/user-profile-data")
    public ModelAndView saveUserProfileData(@ModelAttribute UserProfile userProfileTemp, @RequestParam("userProfileId") String userProfileId, @RequestParam("sex") String sex, @RequestParam("hobbyValues") String hobbyValues, Authentication authentication, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();

        List<String> hobbyValuesList = List.of(hobbyValues.split(","));

        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId);
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();

            userProfile.setAge(userProfileTemp.getAge());
            userProfile.setCity(userProfileTemp.getCity());
            userProfile.setFieldOfStudy(userProfileTemp.getFieldOfStudy());
            userProfile.setRelationshipStatus(userProfileTemp.getRelationshipStatus());
            userProfile.setSex(sex.charAt(0));

            try {
                userProfile.setHobbyIds(this.userProfileService.getHobbyIds(hobbyValuesList));
            } catch (NullPointerException e) {
                modelAndView.setViewName("navbarMenuPages/userProfileData");

                modelAndView.addObject("hobbies", hobbyValuesList);
                modelAndView.addObject("errorHobbies", new InvalidUserHobbiesExceptions().getMessage());
            }

            if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
                String googleId = oAuth2AuthenticationToken.getPrincipal().getAttribute("sub");
                UserAccount userAccount = this.userAccountRepository.findByGoogleId(googleId);

                modelAndView.addObject("userProfile", userProfile);
                this.userProfileService.processUserProfileFromForm(modelAndView, userProfile, userAccount, "user-profile-data", "navbarMenuPages/userProfileData");

                redirectAttributes.addFlashAttribute("displaySuccessModal", "1");
            } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
                String login = usernamePasswordAuthenticationToken.getName();
                UserAccount userAccount = this.userAccountRepository.findByLogin(login);

                modelAndView.addObject("userProfile", userProfile);
                this.userProfileService.processUserProfileFromForm(modelAndView, userProfile, userAccount, "user-profile-data", "navbarMenuPages/userProfileData");

                redirectAttributes.addFlashAttribute("displaySuccessModal", "1");
            }
        }

        return modelAndView;
    }



}
