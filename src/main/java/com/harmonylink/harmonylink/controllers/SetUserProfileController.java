package com.harmonylink.harmonylink.controllers;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.City;
import com.harmonylink.harmonylink.models.user.userprofile.Education;
import com.harmonylink.harmonylink.models.user.userprofile.Hobby;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.CityRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.EducationRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.HobbyRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

import static com.harmonylink.harmonylink.utils.UserUtil.createPageableWithLimit;

@Controller
public class SetUserProfileController {

    private final UserProfileService userProfileService;
    private final UserAccountRepository userAccountRepository;
    private final EducationRepository educationRepository;
    private final CityRepository cityRepository;
    private final HobbyRepository hobbyRepository;


    @Autowired
    public SetUserProfileController(UserProfileService userProfileService, UserAccountRepository userAccountRepository, EducationRepository educationRepository, CityRepository cityRepository, HobbyRepository hobbyRepository) {
        this.userProfileService = userProfileService;
        this.userAccountRepository = userAccountRepository;
        this.educationRepository = educationRepository;
        this.cityRepository = cityRepository;
        this.hobbyRepository = hobbyRepository;
    }


    @GetMapping("/studies")
    @ResponseBody
    public List<String> getEducations(@RequestParam("prefix") String prefix) {
        Pageable pageable = createPageableWithLimit(5);
        List<Education> educations = this.educationRepository.findEducationStartWith(prefix, pageable);

        return educations.stream().map(Education::getName).collect(Collectors.toList());
    }

    @GetMapping("/cities")
    @ResponseBody
    public List<String> getCities(@RequestParam("prefix") String prefix) {
        Pageable pageable = createPageableWithLimit(5);
        List<City> cities = this.cityRepository.findCitiesStartingWith(prefix, pageable);

        return cities.stream().map(City::getName).collect(Collectors.toList());
    }

    @GetMapping("/hobbies")
    @ResponseBody
    public List<String> getHobbies(@RequestParam("prefix") String prefix) {
        Pageable pageable = createPageableWithLimit(5);
        List<Hobby> hobbies = this.hobbyRepository.findHobbiesStartingWith(prefix, pageable);

        return hobbies.stream().map(Hobby::getName).collect(Collectors.toList());
    }


    @GetMapping("/set-profile")
    public String showSetUserProfile(Model model, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");

            UserAccount userAccount = this.userAccountRepository.findByEmail(email);

            model.addAttribute("googleId", userAccount.getGoogleId());
        }

        return "setUserProfileData";
    }

    @PostMapping("/set-profile")
    public ModelAndView setUserProfile(@ModelAttribute UserProfile userProfile, @RequestParam("hobbyValues") String hobbyValues, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            List<String> hobbyValuesList = List.of(hobbyValues.split(","));
            userProfile.setHobbyIds(this.userProfileService.getHobbyIds(hobbyValuesList));
        } catch (NullPointerException e) {
            modelAndView.setViewName("setUserProfileData");
            modelAndView.addObject("errorHobbies", new InvalidUserHobbiesExceptions().getMessage());
        }

        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            String googleId = oAuth2AuthenticationToken.getPrincipal().getAttribute("sub");
            UserAccount userAccount = this.userAccountRepository.findByGoogleId(googleId);

            modelAndView.addObject("userProfile", userProfile);
            this.userProfileService.processUserProfileFromForm(modelAndView, userProfile, userAccount, "", "setUserProfileData");
        } else if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            String login = usernamePasswordAuthenticationToken.getName();
            UserAccount userAccount = this.userAccountRepository.findByLogin(login);

            modelAndView.addObject("userProfile", userProfile);
            this.userProfileService.processUserProfileFromForm(modelAndView, userProfile, userAccount, "", "setUserProfileData");
        }

        return modelAndView;
    }

}
