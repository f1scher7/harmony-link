package com.harmonylink.harmonylink.repositories.token;

import com.harmonylink.harmonylink.models.token.ChangeEmailToken;
import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeEmailTokenRepository extends MongoRepository<ChangeEmailToken, String> {

    ChangeEmailToken findByToken(String token);

    ChangeEmailToken findByUserAccount(UserAccount userAccount);

}
