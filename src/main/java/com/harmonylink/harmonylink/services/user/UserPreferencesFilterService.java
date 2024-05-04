package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserPreferencesFilterService {

    private final UserPreferencesFilterRepository userPreferencesFilterRepository;


    @Autowired
    public UserPreferencesFilterService(UserPreferencesFilterRepository userPreferencesFilterRepository) {
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
    }


    public void saveDefaultUserPreferencesFilters(UserProfile userProfile) {
        UserPreferencesFilter userPreferencesFilter = new UserPreferencesFilter();

        userPreferencesFilter.setUserProfile(userProfile);
        userPreferencesFilter.setAges(new ArrayList<>(Arrays.asList(16, 89)));
        userPreferencesFilter.setHeights(new ArrayList<>(Arrays.asList(150, 230)));

        this.userPreferencesFilterRepository.save(userPreferencesFilter);
    }

    public List<Integer> getIntegerListFromJSON(JSONArray jsonArray) {
        List<Integer> list = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.optInt(i));
            }
        }

        return list;
    }

    public List<String> getStringListFromJSON(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.optString(i));
            }
        }

        return list;
    }


}
