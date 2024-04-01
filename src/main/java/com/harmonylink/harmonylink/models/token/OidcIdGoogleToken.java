package com.harmonylink.harmonylink.models.token;

import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document("oidcId_google_tokens")
public class OidcIdGoogleToken {

    @Id
    private String id;
    @Indexed(unique = true)
    private String tokenId;
    @Field
    private String issuer;
    @DBRef
    private UserAccount userAccount;
    @Field
    private Date issuedAt;
    @Field
    @Indexed(expireAfterSeconds = 4000)
    private Date expiresAt;


    public OidcIdGoogleToken() {}

    public OidcIdGoogleToken(String tokenId, String issuer, UserAccount userAccount, Date issuedAt, Date expiresAt) {
        this.tokenId = tokenId;
        this.issuer = issuer;
        this.userAccount = userAccount;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }


    public String getId() {
        return this.id;
    }

    public String getTokenId() {
        return this.tokenId;
    }

    public String getIssuer() {
        return this.issuer;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public Date getIssuedAt() {
        return this.issuedAt;
    }

    public Date getExpiresAt() {
        return this.expiresAt;
    }


    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }


    @Override
    public String toString() {
        return "OidcIdToken{" +
                "id='" + id + '\'' +
                ", tokenId='" + tokenId + '\'' +
                ", issuer='" + issuer + '\'' +
                ", userAccount=" + userAccount +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }

}
