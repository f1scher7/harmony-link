package com.harmonylink.harmonylink.repositories.token;

import com.harmonylink.harmonylink.models.token.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

    VerificationToken findByToken(String token);

    VerificationToken getUserAccountByToken(String token);

}