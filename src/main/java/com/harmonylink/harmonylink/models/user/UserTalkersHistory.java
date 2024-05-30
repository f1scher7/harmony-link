package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.models.user.userdata.UserTalkerData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Document("users_talkers_history")
public class UserTalkersHistory {

    @Id
    private String userTalkersHistoryId;
    @DBRef
    private UserProfile userProfile;
    @Field
    private List<UserTalkerData> talkers;


    public UserTalkersHistory() {
        this.talkers = new ArrayList<>();
    }

    public UserTalkersHistory(UserProfile userProfile, List<UserTalkerData> talkers) {
        this.userProfile = userProfile;
        this.talkers = talkers;
    }


    public void addTalker(UserTalkerData userTalkerData) {
        this.talkers.add(userTalkerData);
    }


    public String getUserTalkersHistoryId() {
        return this.userTalkersHistoryId;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public List<UserTalkerData> getTalkers() {
        return this.talkers;
    }


    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setTalkers(List<UserTalkerData> userTalkerDataList) {
        this.talkers = userTalkerDataList;
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
