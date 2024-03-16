package com.harmonylink.harmonylink.repositories.user.token;

import com.harmonylink.harmonylink.models.user.token.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {}
