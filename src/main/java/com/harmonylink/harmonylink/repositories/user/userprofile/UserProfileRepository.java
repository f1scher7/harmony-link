package com.harmonylink.harmonylink.repositories.user.userprofile;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

    UserProfile findByUserAccount(UserAccount userAccount);

    UserProfile findByNickname(String nickname);

}
