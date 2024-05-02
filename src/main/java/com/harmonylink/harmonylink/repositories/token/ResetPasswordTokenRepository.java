package com.harmonylink.harmonylink.repositories.token;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.token.ResetPasswordToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordTokenRepository extends MongoRepository<ResetPasswordToken, String> {

    ResetPasswordToken findByToken(String token);

    ResetPasswordToken findByUserAccount(UserAccount userAccount);

}