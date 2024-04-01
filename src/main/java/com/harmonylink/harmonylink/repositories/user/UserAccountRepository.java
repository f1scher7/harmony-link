package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.enums.Role;
import com.harmonylink.harmonylink.models.user.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAccountRepository extends MongoRepository<UserAccount, String> {

    UserAccount findByLogin (String login);

    UserAccount findByEmail (String email);

    UserAccount findByGoogleId(String googleId);

    List<UserAccount> findAllByRole(Role role);

    void deleteUserAccountByLogin(String login);

}
