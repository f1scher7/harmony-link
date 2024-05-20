package com.harmonylink.harmonylink.controllers.home;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

import static com.harmonylink.harmonylink.utils.JSONUtil.getIntegerListFromJSON;
import static com.harmonylink.harmonylink.utils.JSONUtil.getStringListFromJSON;

@Controller
public class UserPreferencesFilterController {

    private final UserProfileService userProfileService;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;
    private final UserProfileRepository userProfileRepository;


    @Autowired
    public UserPreferencesFilterController(UserProfileService userProfileService, UserPreferencesFilterRepository userPreferencesFilterRepository, UserProfileRepository userProfileRepository) {
        this.userProfileService = userProfileService;
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
        this.userProfileRepository = userProfileRepository;
    }


    @PostMapping("/user-preferences-data")
    @ResponseBody
    public void getAndSaveUserPreferences(@RequestBody String body) throws JSONException {
        JSONObject jsonObject = new JSONObject(body);

        JSONArray agesArray = jsonObject.optJSONArray("ages");
        JSONArray heightsArray = jsonObject.optJSONArray("heights");
        JSONArray citiesArray = jsonObject.optJSONArray("cities");
        JSONArray hobbiesArray = jsonObject.optJSONArray("hobbies");
        JSONArray studiesArray = jsonObject.optJSONArray("studies");

        String userProfileId = jsonObject.optString("userProfileId");
        String relationShipStatus = jsonObject.optString("relationshipStatus");
        String sex = jsonObject.optString("sex");


        List<Integer> ages = getIntegerListFromJSON(agesArray);
        List<Integer> heights = getIntegerListFromJSON(heightsArray);
        List<String> cities = getStringListFromJSON(citiesArray);

        List<String> hobbies = getStringListFromJSON(hobbiesArray);
        List<String> hobbyIds = this.userProfileService.getHobbyIds(hobbies);

        List<String> studies = getStringListFromJSON(studiesArray);

        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId);

        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();

            UserPreferencesFilter userPreferencesFilter = this.userPreferencesFilterRepository.findByUserProfile(userProfile);

            userPreferencesFilter.setUserProfile(userProfile);
            userPreferencesFilter.setAges(ages);
            userPreferencesFilter.setHeights(heights);
            userPreferencesFilter.setCities(cities);
            userPreferencesFilter.setFieldsOfStudy(studies);
            userPreferencesFilter.setHobbyIds(hobbyIds);
            userPreferencesFilter.setRelationshipStatus(relationShipStatus);
            userPreferencesFilter.setSex(sex);

            this.userPreferencesFilterRepository.save(userPreferencesFilter);
        }
    }

}
