package com.harmonylink.harmonylink.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Document("user_accounts")
public class User {

    @Id
    private String id;
    @Indexed(unique = true)
    private String login;
    @Field
    private String password;
    @Field
    private String nickname;
    @Indexed(unique = true)
    private String email;
    @Field
    private LocalDate birthdate;
    @Field
    private Character sex;


    public User() {}

    public User(String login, String password, String nickname, String email, LocalDate birthdate, Character sex) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.birthdate = birthdate;
        this.sex = sex;
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

    public String getNickname() {
        return this.nickname;
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

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", sex=" + sex +
                '}';
    }
}
