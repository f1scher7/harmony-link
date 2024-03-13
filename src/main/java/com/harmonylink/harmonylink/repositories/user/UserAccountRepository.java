package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    UserAccount findByLogin (String login);

    UserAccount findByEmail (String email);

    UserAccount findByPasswordResetToken(String token);

    void deleteUserAccountByLogin(String login);

}
