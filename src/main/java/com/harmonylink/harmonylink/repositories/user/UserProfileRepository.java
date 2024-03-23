package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.models.user.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

}
