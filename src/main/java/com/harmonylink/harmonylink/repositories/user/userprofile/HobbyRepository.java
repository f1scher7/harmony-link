package com.harmonylink.harmonylink.repositories.user.userprofile;

import com.harmonylink.harmonylink.models.user.userprofile.Hobby;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HobbyRepository extends MongoRepository<Hobby, String> {

    Hobby findByName(String name);

}
