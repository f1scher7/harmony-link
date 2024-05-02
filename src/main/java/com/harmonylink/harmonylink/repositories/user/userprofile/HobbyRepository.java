package com.harmonylink.harmonylink.repositories.user.userprofile;

import com.harmonylink.harmonylink.models.user.userprofile.Hobby;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyRepository extends MongoRepository<Hobby, String> {

    Hobby findByName(String name);

    @Query("{ 'name': { '$regex': '?0', '$options': 'i' } }")
    List<Hobby> findHobbiesStartingWith(String regex, Pageable pageable);

}
