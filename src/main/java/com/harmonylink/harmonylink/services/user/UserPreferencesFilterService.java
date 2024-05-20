package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

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

}
