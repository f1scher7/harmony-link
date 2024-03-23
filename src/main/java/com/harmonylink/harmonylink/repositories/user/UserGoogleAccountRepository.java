package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.models.user.UserGoogleAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserGoogleAccountRepository extends MongoRepository<UserGoogleAccount, String> {}
