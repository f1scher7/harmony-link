package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.models.user.UserTalkersHistory;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTalkersHistoryRepository extends MongoRepository<UserTalkersHistory, String> {

    UserTalkersHistory findByUserProfile(UserProfile userProfile);

}
