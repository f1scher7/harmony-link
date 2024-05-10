package com.harmonylink.harmonylink.models.user.userdata;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;

public class UserSearchData {

    private final UserProfile userProfile;
    private final UserPreferencesFilter userPreferencesFilter;


    public UserSearchData(UserProfile userProfile, UserPreferencesFilter userPreferencesFilter) {
        this.userProfile = userProfile;
        this.userPreferencesFilter = userPreferencesFilter;
    }


    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public UserPreferencesFilter getUserPreferencesFilter() {
        return this.userPreferencesFilter;
    }


    @Override
    public String toString() {
        return "UserSearchData{" +
                "userProfile=" + this.userProfile +
                ", userPreferencesFilter=" + this.userPreferencesFilter +
                '}';
    }

}
