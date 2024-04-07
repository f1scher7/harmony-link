package com.harmonylink.harmonylink.models.user.userprofile;

import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

import static com.harmonylink.harmonylink.utils.UserUtil.getUserAge;

@Document("user_profiles")
public class UserProfile {

    @Id
    private String id;
    @DBRef
    private UserAccount userAccount;
    @Field
    private boolean isOnline;
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

    public UserProfile(UserAccount userAccount, String city, int height, String relationshipStatus, List<String> hobbyIds, String fieldOfStudy) {
        this.userAccount = userAccount;
        this.city = city;
        this.sex = userAccount.getSex();
        this.age = getUserAge(userAccount.getBirthdate());
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

    public String getCity() {
        return this.city;
    }

    public boolean isOnline() {
        return this.isOnline;
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

    public void setCity(String city) {
        this.city = city;
    }

    public void setOnline(boolean online) {
        this.isOnline = online;
    }

    public void setSex() {
        this.sex = this.userAccount.getSex();
    }

    public void setAge(LocalDate birthdate) {
        this.age = getUserAge(birthdate);
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
                ", isOnline=" + this.isOnline +
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
