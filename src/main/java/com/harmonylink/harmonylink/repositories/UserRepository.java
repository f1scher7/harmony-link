package com.harmonylink.harmonylink.repositories;

import com.harmonylink.harmonylink.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByLogin (String login);

    User findByEmail (String email);
}
