package com.harmonylink.harmonylink.repositories.user.tokens;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.tokens.ResetPasswordToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResetPasswordTokenRepository extends MongoRepository<ResetPasswordToken, String> {

    ResetPasswordToken findByToken(String token);

    ResetPasswordToken findByUserAccount(UserAccount userAccount);

}
