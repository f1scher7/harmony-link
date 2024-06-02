package com.harmonylink.harmonylink.controllers.navbarmenupages;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.services.user.useraccount.UserAccountSettingsService;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserAccountSettingsController {

    private final UserProfileService userProfileService;
    private final UserAccountSettingsService userAccountSettingsService;


    @Autowired
    public UserAccountSettingsController(UserProfileService userProfileService, UserAccountSettingsService userAccountSettingsService) {
        this.userProfileService = userProfileService;
        this.userAccountSettingsService = userAccountSettingsService;
    }


    @GetMapping("/account-settings")
    public String getAccountSettingsPage(Model model, Authentication authentication) {
        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);

        model.addAttribute("userProfileNickname", userProfile.getNickname());

        return "navbarMenuPages/accountSettings";
    }


    @PostMapping("/change-password")
    public ModelAndView changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, @RequestParam String confirmPassword, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);

        try {
            this.userAccountSettingsService.changePassword(userProfile.getNickname(), oldPassword, newPassword, confirmPassword);

            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.addObject("displayChangePasswordSuccessModal", "1");
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        } catch (UserNotFoundException e) {
            modelAndView.addObject("errorUserNotFound", e.getMessage());
            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        } catch (WrongPasswordException e) {
            modelAndView.addObject("errorWrongPassword", e.getMessage());
            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        } catch (InvalidPasswordException e) {
            modelAndView.addObject("errorInvalidNewPassword", e.getMessage());
            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        } catch (PasswordsMatchingException e) {
            modelAndView.addObject("errorPasswordsMatching", e.getMessage());
            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        }

        return modelAndView;
    }

}
