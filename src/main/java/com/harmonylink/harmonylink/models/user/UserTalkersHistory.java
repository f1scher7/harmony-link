package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document("users_talkers_history")
public class UserTalkersHistory {

    @Id
    private String UserTalkersHistoryId;
    @DBRef
    private UserProfile userProfile;
    @Field
    private List<UserProfile> talkers;


    public UserTalkersHistory() {
        this.talkers = new ArrayList<>();
    }

    public UserTalkersHistory(UserProfile userProfile, List<UserProfile> talkers) {
        this.userProfile = userProfile;
        this.talkers = talkers;
    }


    public void addTalker(UserProfile userProfile) {
        if (!this.talkers.contains(userProfile)) {
            this.talkers.add(userProfile);
        }
    }


    public String getUserTalkersHistoryId() {
        return this.UserTalkersHistoryId;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public List<UserProfile> getTalkers() {
        return this.talkers;
    }


    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setTalkers(List<UserProfile> talkers) {
        this.talkers = talkers;
    }


    @Override
    public String toString() {
        return "UserTalkersHistory{" +
                "UserTalkersHistoryId='" + this.UserTalkersHistoryId + '\'' +
                ", userProfile=" + this.userProfile +
                ", talkers=" + this.talkers +
                '}';
    }

}
