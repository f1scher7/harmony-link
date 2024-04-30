package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("user_activity_statuses")
public class UserActivityStatus {

    @Id
    private String userActivityStatusId;
    @DBRef
    private UserProfile userProfile;
    @Field
    private UserActivityStatusEnum activityStatus;


    public UserActivityStatus() {}

    public UserActivityStatus(UserProfile userProfile, UserActivityStatusEnum userActivityStatus) {
        this.userProfile = userProfile;
        this.activityStatus = userActivityStatus;
    }


    public String getUserActivityStatusId() {
        return this.userActivityStatusId;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public UserActivityStatusEnum getUserActivityStatus() {
        return this.activityStatus;
    }


    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setUserActivityStatus(UserActivityStatusEnum userActivityStatus) {
        this.activityStatus = userActivityStatus;
    }


    @Override
    public String toString() {
        return "UserActivityStatus{" +
                "userActivityStatusId='" + this.userActivityStatusId + '\'' +
                ", userProfile=" + this.userProfile +
                ", userActivityStatus=" + this.activityStatus +
                '}';
    }

}
