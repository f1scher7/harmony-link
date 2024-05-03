package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesFilterRepository extends MongoRepository<UserPreferencesFilter, String> {

    UserPreferencesFilter findByUserProfile(UserProfile userProfile);

}
