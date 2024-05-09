package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;

public class UserSearchData {

    private UserProfile userProfile;
    private UserPreferencesFilter userPreferencesFilter;


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


    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setUserPreferencesFilter(UserPreferencesFilter userPreferencesFilter) {
        this.userPreferencesFilter = userPreferencesFilter;
    }


    @Override
    public String toString() {
        return "UserSearchData{" +
                "userProfile=" + this.userProfile +
                ", userPreferencesFilter=" + this.userPreferencesFilter +
                '}';
    }

}
