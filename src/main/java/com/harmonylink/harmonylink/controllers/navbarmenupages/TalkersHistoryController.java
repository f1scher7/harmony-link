package com.harmonylink.harmonylink.controllers.navbarmenupages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmonylink.harmonylink.models.user.UserTalkersHistory;
import com.harmonylink.harmonylink.models.user.userdata.UserTalkerData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserTalkersHistoryRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.InvalidUserHobbiesExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class TalkersHistoryController {

    private final UserProfileService userProfileService;
    private final UserTalkersHistoryRepository userTalkersHistoryRepository;
    private final UserProfileRepository userProfileRepository;


    @Autowired
    public TalkersHistoryController(UserProfileService userProfileService, UserTalkersHistoryRepository userTalkersHistoryRepository, UserProfileRepository userProfileRepository) {
        this.userProfileService = userProfileService;
        this.userTalkersHistoryRepository = userTalkersHistoryRepository;
        this.userProfileRepository = userProfileRepository;
    }


    @GetMapping("/talkers-history")
    public String getTalkersHistoryPage(Model model, Authentication authentication) throws JsonProcessingException, InvalidUserHobbiesExceptions {
        UserProfile userProfile = this.userProfileService.getUserProfileByAuthentication(authentication);

        UserTalkersHistory userTalkersHistory = this.userTalkersHistoryRepository.findByUserProfile(userProfile);

        List<UserTalkerData> userTalkerDataList = userTalkersHistory.getTalkers();
        Collections.reverse(userTalkerDataList);

        List<Map<String, Object>> userTalkersDataForJSON = new ArrayList<>();
        for (UserTalkerData talkerData : userTalkerDataList) {
            Map<String, Object> userDataMap = new HashMap<>();
            UserProfile userProfileForJSON = this.userProfileRepository.findByNickname(talkerData.getUserProfile().getNickname());

            List<String> hobbies = this.userProfileService.getHobbies(userProfileForJSON.getHobbyIds());

            String sex = "";
            String relationshipStatus = "";

            if (String.valueOf(userProfileForJSON.getSex()).equals("M")) {
                sex = "Mężczyzna";
            } else if (String.valueOf(userProfileForJSON.getSex()).equals("K")) {
                sex = "Kobieta";
            }

            relationshipStatus = switch (userProfileForJSON.getRelationshipStatus()) {
                case "single" -> "Singiel/Singielka";
                case "relationship" -> "W związku";
                case "married" -> "Żonaty/Zamężna";
                case "divorced" -> "Wszystko skomplikowane";
                default -> relationshipStatus;
            };

            userDataMap.put("localDateTime", talkerData.getLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
            userDataMap.put("nickname", userProfileForJSON.getNickname());
            userDataMap.put("city", userProfileForJSON.getCity());
            userDataMap.put("age", userProfileForJSON.getAge());
            userDataMap.put("sex", sex);
            userDataMap.put("relationshipStatus", relationshipStatus);
            userDataMap.put("hobby", hobbies);
            userDataMap.put("study", userProfileForJSON.getFieldOfStudy());

            userTalkersDataForJSON.add(userDataMap);
        }

        String userTalkersDataJSON = new ObjectMapper().writeValueAsString(userTalkersDataForJSON);

        model.addAttribute("userTalkersDataJSON", userTalkersDataJSON);
        model.addAttribute("userProfileNickname", userProfile.getNickname());

        return "navbarMenuPages/talkersHistory";
    }

}
