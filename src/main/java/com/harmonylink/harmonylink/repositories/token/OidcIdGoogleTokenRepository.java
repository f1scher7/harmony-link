package com.harmonylink.harmonylink.repositories.token;

import com.harmonylink.harmonylink.models.token.OidcIdGoogleToken;
import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OidcIdGoogleTokenRepository extends MongoRepository<OidcIdGoogleToken, String> {

    OidcIdGoogleToken findByUserAccount(UserAccount userAccount);

}
