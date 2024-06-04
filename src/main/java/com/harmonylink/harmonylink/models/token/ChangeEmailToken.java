package com.harmonylink.harmonylink.models.token;

import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document("change_email_tokens")
public class ChangeEmailToken {

    @Id
    private String id;
    @Indexed(unique = true)
    private String token;
    @DBRef
    private UserAccount userAccount;
    @Field
    @Indexed(expireAfterSeconds = 3600)
    private LocalDateTime localDateTime;


    public ChangeEmailToken(String token, UserAccount userAccount) {
        this.token = token;
        this.userAccount = userAccount;
        this.localDateTime = LocalDateTime.now();
    }


    public String getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }


}
