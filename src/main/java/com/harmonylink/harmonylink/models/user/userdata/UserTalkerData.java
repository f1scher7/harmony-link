package com.harmonylink.harmonylink.models.user.userdata;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;

import java.time.LocalDateTime;

public class UserTalkerData {

    private UserProfile userProfile;
    private LocalDateTime localDateTime;


    public UserTalkerData() {
        this.localDateTime = LocalDateTime.now();
    }

    public UserTalkerData(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.localDateTime = LocalDateTime.now();
    }


    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }



    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

}
