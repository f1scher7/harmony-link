package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document("users_preferences_filters")
public class UserPreferencesFilter {

    @Id
    private String UserPreferenceFiltersId;
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
    private List<String> relationshipStatuses;
    @Field
    private List<String> hobbyIds;
    @Field
    private List<String> fieldsOfStudy;


    public UserPreferencesFilter() {
        this.cities = new ArrayList<>();
        this.ages = new ArrayList<>();
        this.heights = new ArrayList<>();
        this.relationshipStatuses = new ArrayList<>();
        this.hobbyIds = new ArrayList<>();
        this.fieldsOfStudy = new ArrayList<>();
    }

    public UserPreferencesFilter(UserProfile userProfile, List<String> cities, List<Integer> ages, List<Integer> heights, List<String> relationshipStatuses, List<String> hobbyIds, List<String> fieldsOfStudy) {
        this.userProfile = userProfile;
        this.cities = cities;
        this.ages = ages;
        this.heights = heights;
        this.relationshipStatuses = relationshipStatuses;
        this.hobbyIds = hobbyIds;
        this.fieldsOfStudy = fieldsOfStudy;
    }


    public String getUserPreferenceFiltersId() {
        return this.UserPreferenceFiltersId;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public List<String> getCities() {
        return this.cities;
    }

    public String getSexes() {
        return this.sex;
    }

    public List<Integer> getAges() {
        return this.ages;
    }

    public List<Integer> getHeights() {
        return this.heights;
    }

    public List<String> getRelationshipStatuses() {
        return this.relationshipStatuses;
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

    public void setSexes(String sex) {
        this.sex = sex;
    }

    public void setAges(List<Integer> ages) {
        this.ages = ages;
    }

    public void setHeights(List<Integer> heights) {
        this.heights = heights;
    }

    public void setRelationshipStatuses(List<String> relationshipStatuses) {
        this.relationshipStatuses = relationshipStatuses;
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
                "UserPreferenceFiltersId='" + UserPreferenceFiltersId + '\'' +
                ", userProfile=" + userProfile +
                ", cities=" + cities +
                ", sex=" + sex +
                ", ages=" + ages +
                ", heights=" + heights +
                ", relationshipStatuses=" + relationshipStatuses +
                ", hobbyIds=" + hobbyIds +
                ", fieldsOfStudy=" + fieldsOfStudy +
                '}';
    }

}
