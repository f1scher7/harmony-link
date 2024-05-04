package com.harmonylink.harmonylink.controllers.home;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.UserActivityStatusRepository;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final UserProfileService userProfileService;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserActivityStatusRepository userActivityStatusRepository;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;


    @Autowired
    public HomeController(UserProfileService userProfileService, UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository, UserActivityStatusRepository userActivityStatusRepository, UserPreferencesFilterRepository userPreferencesFilterRepository) {
        this.userProfileService = userProfileService;
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.userActivityStatusRepository = userActivityStatusRepository;
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
    }


    @GetMapping("/users-activity-status")
    @ResponseBody
    public List<Long> getUsersActivityStatus() {
        List<Long> usersActivityStatistic = new ArrayList<>();
        long in_search = this.userActivityStatusRepository.countByActivityStatus(UserActivityStatusEnum.IN_SEARCH);
        long in_call = this.userActivityStatusRepository.countByActivityStatus(UserActivityStatusEnum.IN_CALL);
        long online = this.userActivityStatusRepository.countByActivityStatus(UserActivityStatusEnum.ONLINE) + in_search + in_call;

        usersActivityStatistic.add(online);
        usersActivityStatistic.add(in_search);
        usersActivityStatistic.add(in_call);

        return usersActivityStatistic;
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

        UserPreferencesFilter userPreferencesFilter = this.userPreferencesFilterRepository.findByUserProfile(userProfile);
        List<String> hobbies = this.userProfileService.getHobbies(userPreferencesFilter.getHobbyIds());

        model.addAttribute("userProfile", userProfile);
        model.addAttribute("sex", userPreferencesFilter.getSex());
        model.addAttribute("relationshipStatus", userPreferencesFilter.getRelationshipStatus());
        model.addAttribute("ages", userPreferencesFilter.getAges());
        model.addAttribute("heights", userPreferencesFilter.getHeights());
        model.addAttribute("cities", userPreferencesFilter.getCities());
        model.addAttribute("hobbies", hobbies);
        model.addAttribute("studies", userPreferencesFilter.getFieldsOfStudy());

        return "home";
    }

}
