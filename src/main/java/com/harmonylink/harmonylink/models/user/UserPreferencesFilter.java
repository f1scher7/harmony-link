package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document("user_preferences_filters")
public class UserPreferencesFilter {

    @Id
    private String userPreferenceFilterId;
    @DBRef
    private UserProfile userProfile;
    @Field
    private List<String> cities;
    @Field
    private String sex;
    @Field
    private List<Integer> ages;
    @Field
    private List<Integer> heights;
    @Field
    private String relationshipStatus;
    @Field
    private List<String> hobbyIds;
    @Field
    private List<String> fieldsOfStudy;


    public UserPreferencesFilter() {
        this.cities = new ArrayList<>();
        this.sex = "";
        this.ages = new ArrayList<>();
        this.heights = new ArrayList<>();
        this.relationshipStatus = "";
        this.hobbyIds = new ArrayList<>();
        this.fieldsOfStudy = new ArrayList<>();
    }

    public UserPreferencesFilter(UserProfile userProfile, List<String> cities, List<Integer> ages, List<Integer> heights, String relationshipStatus, List<String> hobbyIds, List<String> fieldsOfStudy) {
        this.userProfile = userProfile;
        this.cities = cities;
        this.ages = ages;
        this.heights = heights;
        this.relationshipStatus = relationshipStatus;
        this.hobbyIds = hobbyIds;
        this.fieldsOfStudy = fieldsOfStudy;
    }


    public String getUserPreferenceFilterId() {
        return this.userPreferenceFilterId;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public List<String> getCities() {
        return this.cities;
    }

    public String getSex() {
        return this.sex;
    }

    public List<Integer> getAges() {
        return this.ages;
    }

    public List<Integer> getHeights() {
        return this.heights;
    }

    public String getRelationshipStatus() {
        return this.relationshipStatus;
    }

    public List<String> getHobbyIds() {
        return this.hobbyIds;
    }

    public List<String> getFieldsOfStudy() {
        return this.fieldsOfStudy;
    }


    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setAges(List<Integer> ages) {
        this.ages = ages;
    }

    public void setHeights(List<Integer> heights) {
        this.heights = heights;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public void setHobbyIds(List<String> hobbyIds) {
        this.hobbyIds = hobbyIds;
    }

    public void setFieldsOfStudy(List<String> fieldsOfStudy) {
        this.fieldsOfStudy = fieldsOfStudy;
    }


    @Override
    public String toString() {
        return "UserPreferencesFilter{" +
                "UserPreferenceFilterId='" + this.userPreferenceFilterId + '\'' +
                ", userProfile=" + this.userProfile +
                ", cities=" + this.cities +
                ", sex=" + this.sex +
                ", ages=" + this.ages +
                ", heights=" + this.heights +
                ", relationshipStatus=" + this.relationshipStatus +
                ", hobbyIds=" + this.hobbyIds +
                ", fieldsOfStudy=" + this.fieldsOfStudy +
                '}';
    }

}
