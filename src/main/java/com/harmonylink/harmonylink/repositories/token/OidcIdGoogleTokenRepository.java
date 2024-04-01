package com.harmonylink.harmonylink.repositories.token;

import com.harmonylink.harmonylink.models.token.OidcIdGoogleToken;
import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OidcIdGoogleTokenRepository extends MongoRepository<OidcIdGoogleToken, String> {

    OidcIdGoogleToken findByUserAccount(UserAccount userAccount);

}
