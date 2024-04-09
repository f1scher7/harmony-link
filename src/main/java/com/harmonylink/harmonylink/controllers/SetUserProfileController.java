package com.harmonylink.harmonylink.controllers;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserNotFoundException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserTooYoungException;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SetUserProfileController {

    private final UserProfileService userProfileService;
    private final UserAccountRepository userAccountRepository;


    @Autowired
    public SetUserProfileController(UserProfileService userProfileService, UserAccountRepository userAccountRepository) {
        this.userProfileService = userProfileService;
        this.userAccountRepository = userAccountRepository;
    }


    @GetMapping("/set-userprofile")
    public String showSetUserProfile(Model model, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");

            UserAccount userAccount = this.userAccountRepository.findByEmail(email);

            model.addAttribute("googleId", userAccount.getGoogleId());
        }

        return "setUserProfileData";
    }

    @PostMapping("/set-userprofile")
    public ModelAndView setUserProfile(@ModelAttribute UserProfile userProfile, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            String googleId = oAuth2AuthenticationToken.getPrincipal().getAttribute("sub");
            UserAccount userAccount = this.userAccountRepository.findByGoogleId(googleId);

            processUserProfile(modelAndView, userProfile, userAccount);

        } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            String login = usernamePasswordAuthenticationToken.getName();
            UserAccount userAccount = this.userAccountRepository.findByLogin(login);

            processUserProfile(modelAndView, userProfile, userAccount);
        }

        return modelAndView;
    }

    private void processUserProfile(ModelAndView modelAndView, UserProfile userProfile, UserAccount userAccount) {
        try {
            this.userProfileService.setOrUpdateUserProfileData(userProfile, userAccount.getId());
            modelAndView.setViewName("redirect:/");
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvalidUserCityException e) {
            modelAndView.setViewName("setUserProfileData");
            modelAndView.addObject("errorCity", e.getMessage());
        } catch (InvalidRelationshipStatusException e) {
            modelAndView.setViewName("setUserProfileData");
            modelAndView.addObject("errorRelationship", e.getMessage());
        } catch (InvalidUserHobbiesExceptions e) {
            modelAndView.setViewName("setUserProfileData");
            modelAndView.addObject("errorHobbies", e.getMessage());
        } catch (InvalidUserHeightException e) {
            modelAndView.setViewName("setUserProfileData");
            modelAndView.addObject("errorHeight", e.getMessage());
        } catch (InvalidUserFieldOfStudyException e) {
            modelAndView.setViewName("setUserProfileData");
            modelAndView.addObject("errorStudy", e.getMessage());
        } catch (UserTooYoungException e) {
            modelAndView.setViewName("setUserProfileData");
            modelAndView.addObject("errorAge", e.getMessage());
        }
    }

}
