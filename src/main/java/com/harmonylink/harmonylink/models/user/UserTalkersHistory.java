package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

@Document("users_talkers_history")
public class UserTalkersHistory {

    @Id
    private String userTalkersHistoryId;
    @DBRef
    private UserProfile userProfile;
    @Field
    private SortedMap<LocalDate, UserProfile> talkers;



    public UserTalkersHistory() {
        this.talkers = new TreeMap<>();
    }

    public UserTalkersHistory(UserProfile userProfile, SortedMap<LocalDate, UserProfile> talkers) {
        this.userProfile = userProfile;
        this.talkers = talkers;
    }


    public void addTalker(UserProfile userProfile) {
        this.talkers.put(LocalDate.now(), userProfile);
    }


    public String getUserTalkersHistoryId() {
        return this.userTalkersHistoryId;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public SortedMap<LocalDate, UserProfile> getTalkers() {
        return talkers;
    }


    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setTalkers(SortedMap<LocalDate, UserProfile> talkers) {
        this.talkers = talkers;
    }


    @Override
    public String toString() {
        return "UserTalkersHistory{" +
                "UserTalkersHistoryId='" + this.userTalkersHistoryId + '\'' +
                ", userProfile=" + this.userProfile +
                ", talkers=" + this.talkers +
                '}';
    }

}
