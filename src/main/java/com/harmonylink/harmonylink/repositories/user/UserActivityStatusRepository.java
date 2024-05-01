package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.UserActivityStatus;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityStatusRepository extends MongoRepository<UserActivityStatus, String> {

    UserActivityStatus findByUserProfile(UserProfile userProfile);

    Long countByActivityStatus(UserActivityStatusEnum userActivityStatusEnum);

}
