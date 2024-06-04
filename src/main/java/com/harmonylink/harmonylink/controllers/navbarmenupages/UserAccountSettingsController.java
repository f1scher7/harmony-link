package com.harmonylink.harmonylink.controllers.navbarmenupages;

import com.harmonylink.harmonylink.models.token.ChangeEmailToken;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.token.ChangeEmailTokenRepository;
import com.harmonylink.harmonylink.services.user.useraccount.UserAccountSettingsService;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ChangeEmailTokenRepository changeEmailTokenRepository;


    @Autowired
    public UserAccountSettingsController(UserProfileService userProfileService, UserAccountSettingsService userAccountSettingsService, ChangeEmailTokenRepository changeEmailTokenRepository) {
        this.userProfileService = userProfileService;
        this.userAccountSettingsService = userAccountSettingsService;
        this.changeEmailTokenRepository = changeEmailTokenRepository;
    }


    @GetMapping("/account-settings")
    public String getAccountSettingsPage(Model model, Authentication authentication) {
        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);

        model.addAttribute("userProfileNickname", userProfile.getNickname());

        return "navbarMenuPages/accountSettings";
    }


    @PostMapping("/change-password")
    public ModelAndView changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword, Authentication authentication) {
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

    @PostMapping("/change-email")
    public ModelAndView changeEmail(@RequestParam("passwordForEmail") String passwordForEmail, @RequestParam("newEmail") String newEmail, Authentication authentication, HttpServletRequest httpServletRequest) {
        ModelAndView modelAndView = new ModelAndView();

        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);

        try {
            this.userAccountSettingsService.tryToSendChangeEmailEmail(userProfile.getNickname(), passwordForEmail, newEmail);

            httpServletRequest.getSession().setAttribute("newEmail", newEmail);

            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.addObject("displayChangeEmailModal", "1");
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        }  catch (WrongPasswordException e) {
            modelAndView.addObject("newEmail", newEmail);
            modelAndView.addObject("errorWrongPasswordChangingEmail", e.getMessage());
            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        } catch (EmailNotFoundException e) {
            modelAndView.addObject("newEmail", newEmail);
            modelAndView.addObject("errorEmailNotFound", e.getMessage());
            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        } catch (EmailAlreadyExistsException e) {
            modelAndView.addObject("errorEmailAlreadyExists", e.getMessage());
            modelAndView.addObject("userProfileNickname", userProfile.getNickname());
            modelAndView.setViewName("navbarMenuPages/accountSettings");
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }

        return modelAndView;
    }


    @GetMapping("/auth/change-email-success")
    public String showChangeEmailSuccess(@RequestParam("token") String token, HttpServletRequest httpServletRequest) {
        ChangeEmailToken changeEmailToken = this.changeEmailTokenRepository.findByToken(token);
        if (changeEmailToken != null) {
            String newEmail = (String) httpServletRequest.getSession().getAttribute("newEmail");

            this.userAccountSettingsService.changeEmail(changeEmailToken, newEmail);

            return "authPages/changeEmailSuccess";
        } else {
            return "errorTemplates/tokenError";
        }
    }

}
