package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.services.user.useraccount.UserAccountUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("user_accounts")
public class UserAccount {

    @Id
    private String id;
    @Indexed(unique = true)
    private String login;
    @Field
    private String password;
    @Field
    private List<String> ipAddresses;
    @Indexed(unique = true)
    private String email;
    @Field
    private LocalDate birthdate;
    @Field
    private Character sex;


    public UserAccount() {}

    public UserAccount(String login, String password, String email, LocalDate birthdate, Character sex) {
        this.login = login;
        this.password = password;
        this.ipAddresses = new ArrayList<>();
        this.email = email;
        this.birthdate = birthdate;
        this.sex = sex;
    }


    public void addIpAddress(String ipAddress) {
        this.ipAddresses.add(ipAddress);
    }

    public String getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public List<String> getIpAddresses() {
        return this.ipAddresses;
    }

    public String getEmail() {
        return this.email;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public Character getSex() {
        return this.sex;
    }


    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }


    @Override
    public String toString() {
        return "UserAccount{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", sex=" + sex +
                '}';
    }

}
