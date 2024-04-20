package com.harmonylink.harmonylink.repositories.user.userprofile;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    UserProfile findByUserAccount(UserAccount userAccount);

    @Query(value = "{ 'activityStatus': {$in: ['ONLINE', 'IN_CALL', 'IN_SEARCH'] } }", count = true)
    long countOnline();

    @Query(value = "{ 'activityStatus': 'IN_CALL' }", count = true)
    long countByInCallStatus();

    @Query(value = "{ 'activityStatus': 'IN_SEARCH' }", count = true)
    long countByInSearchStatus();

}
