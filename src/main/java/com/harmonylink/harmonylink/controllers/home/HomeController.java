package com.harmonylink.harmonylink.controllers.home;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.CustomMatchesRepository;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.services.user.useractivity.UserWebSocketSessionService;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final UserProfileService userProfileService;
    private final UserWebSocketSessionService userWebSocketSessionService;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;
    private final CustomMatchesRepository customMatchesRepository;


    @Autowired
    public HomeController(UserProfileService userProfileService, UserWebSocketSessionService userWebSocketSessionService, UserPreferencesFilterRepository userPreferencesFilterRepository, CustomMatchesRepository customMatchesRepository) {
        this.userProfileService = userProfileService;
        this.userWebSocketSessionService = userWebSocketSessionService;
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
        this.customMatchesRepository = customMatchesRepository;
    }


    @GetMapping("/users-activity-status")
    @ResponseBody
    public List<Long> getUsersActivityStatus(Authentication authentication) {
        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);
        UserPreferencesFilter userPreferencesFilter = this.userPreferencesFilterRepository.findByUserProfile(userProfile);

        List<Integer> ages = userPreferencesFilter.getAges();
        List<Integer> heights = userPreferencesFilter.getHeights();
        List<String> cities = userPreferencesFilter.getCities();
        List<String> hobbyIds = userPreferencesFilter.getHobbyIds();
        List<String> studies = userPreferencesFilter.getFieldsOfStudy();

        List<Long> usersActivityStatistic = new ArrayList<>();

        long online = this.userWebSocketSessionService.getNumberOfSessions();
        long matchesUserProfilesInSearch = this.customMatchesRepository.countUserProfilesMatchesInSearch(userProfile.getId(), userPreferencesFilter.getSex(), ages.get(0), ages.get(1), heights.get(0), heights.get(1), userPreferencesFilter.getRelationshipStatus(), cities, hobbyIds, studies);
        long matchesUserPreferencesFiltersInSearch = this.customMatchesRepository.countUserPreferencesFiltersMatchesInSearch(userPreferencesFilter.getUserPreferenceFilterId(), Character.toString(userProfile.getSex()), userProfile.getRelationshipStatus(), userProfile.getCity());

        usersActivityStatistic.add(online);
        usersActivityStatistic.add(matchesUserProfilesInSearch);
        usersActivityStatistic.add(matchesUserPreferencesFiltersInSearch);

        return usersActivityStatistic;
    }


    @GetMapping
    public String showHomePage(Model model, Authentication authentication, HttpServletRequest request) {

        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);

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

        HttpSession session = request.getSession();
        if ("true".equals(session.getAttribute("showHarmonyInfoModal"))) {
            model.addAttribute("showHarmonyInfoModal", "true");
            session.removeAttribute("showHarmonyInfoModal");
        }

        return "home";
    }

}
