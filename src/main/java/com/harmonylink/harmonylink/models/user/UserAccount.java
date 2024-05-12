package com.harmonylink.harmonylink.models.user;

import com.harmonylink.harmonylink.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Document("user_accounts")
public class UserAccount implements UserDetails {

    @Id
    private String id;
    @Field
    private String googleId;
    @Field
    private Role role;
    @Indexed(unique = true)
    private String login;
    @Field
    private String displayName;
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
    @Field
    private String profilePictureUrl;
    @Field
    private boolean userAccountNonLocked = true;


    public UserAccount()  {
        this.ipAddresses = new ArrayList<>();
    }

    public UserAccount(String googleId, String displayName, String email, String profilePictureUrl) {
        this.googleId = googleId;
        this.displayName = displayName;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
    }

    public UserAccount(Role role, String login, String password, String email, LocalDate birthdate, Character sex) {
        this.role = role;
        this.login = login;
        this.password = password;
        this.ipAddresses = new ArrayList<>();
        this.email = email;
        this.birthdate = birthdate;
        this.sex = sex;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.userAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public void addIpAddress(String ipAddress) {
        this.ipAddresses.add(ipAddress);
    }


    public void lockAccount() {
        this.userAccountNonLocked = false;
    }

    public void unlockAccount() {
        this.userAccountNonLocked = true;
    }


    public String getId() {
        return this.id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public Role getRole() {
        return this.role;
    }

    public String getLogin() {
        return this.login;
    }

    public String getDisplayName() {
        return displayName;
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public boolean isUserAccountNonLocked() {
        return this.userAccountNonLocked;
    }


    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public void setUserAccountNonLocked(boolean userAccountNonLocked) {
        this.userAccountNonLocked = userAccountNonLocked;
    }


    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "role='" + this.role.name() +'\'' +
                ", id='" + this.id + '\'' +
                ", googleId='" + this.googleId + '\'' +
                ", login='" + this.login + '\'' +
                ", email='" + this.email + '\'' +
                ", birthdate=" + this.birthdate +
                ", sex=" + this.sex +
                '}';
    }

}
