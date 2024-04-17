package com.harmonylink.harmonylink.models.user.userprofile;

import com.harmonylink.harmonylink.enums.UserActivityStatus;
import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("user_profiles")
public class UserProfile {

    @Id
    private String id;
    @DBRef
    private UserAccount userAccount;
    @Field
    private String nickname;
    @Field
    private UserActivityStatus activityStatus;
    @Field
    private String city;
    @Field
    private Character sex;
    @Field
    private int age;
    @Field
    private int height;
    @Field
    private String relationshipStatus;
    @Field
    private List<String> hobbyIds;
    @Field
    private String fieldOfStudy;


    public UserProfile() {}

    public UserProfile(UserAccount userAccount, String nickname, String city, Character sex, int age, int height, String relationshipStatus, List<String> hobbyIds, String fieldOfStudy) {
        this.userAccount = userAccount;
        this.nickname = nickname;
        this.activityStatus = UserActivityStatus.OFFLINE;
        this.city = city;
        this.sex = sex;
        this.age = age;
        this.height = height;
        this.relationshipStatus = relationshipStatus;
        this.hobbyIds = hobbyIds;
        this.fieldOfStudy = fieldOfStudy;
    }


    public void addHobbyId(String hobbyId) {
        if (!this.hobbyIds.contains(hobbyId)) {
            this.hobbyIds.add(hobbyId);
        }
    }


    public String getId() {
        return this.id;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public String getNickname() {
        return this.nickname;
    }

    public UserActivityStatus getActivityStatus() {
        return this.activityStatus;
    }

    public String getCity() {
        return this.city;
    }

    public Character getSex() {
        return this.sex;
    }

    public int getAge() {
        return this.age;
    }

    public int getHeight() {
        return this.height;
    }

    public String getRelationshipStatus() {
        return this.relationshipStatus;
    }

    public List<String> getHobbyIds() {
        return this.hobbyIds;
    }

    public String getFieldOfStudy() {
        return this.fieldOfStudy;
    }


    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setActivityStatus(UserActivityStatus activityStatus) {
        this.activityStatus = activityStatus;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public void setHobbyIds(List<String> hobbies) {
        this.hobbyIds = hobbies;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }


    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + this.id + '\'' +
                ", userAccount=" + this.userAccount +
                ", activityStatus=" + this.activityStatus +
                ", city=" + this.city +
                ", sex=" + this.sex +
                ", age=" + this.age +
                ", height=" + this.height +
                ", relationshipStatus='" + this.relationshipStatus + '\'' +
                ", hobbies=" + this.hobbyIds +
                ", fieldOfStudy='" + this.fieldOfStudy + '\'' +
                '}';
    }

}
