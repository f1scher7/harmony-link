package com.harmonylink.harmonylink.models.user.userdata;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;

public class UserCallPairData {

    private final UserProfile userProfile1;
    private final UserProfile userProfile2;


    public UserCallPairData(UserProfile userProfile1, UserProfile userProfile2) {
        this.userProfile1 = userProfile1;
        this.userProfile2 = userProfile2;
    }


    public UserProfile getUserProfile1() {
        return this.userProfile1;
    }

    public UserProfile getUserProfile2() {
        return this.userProfile2;
    }


    @Override
    public String toString() {
        return "UserCallPairData{" +
                "userProfile1=" + this.userProfile1 +
                ", userProfile2=" + this.userProfile2 +
                '}';
    }

}
