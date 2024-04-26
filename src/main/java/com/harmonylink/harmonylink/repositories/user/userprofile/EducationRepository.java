package com.harmonylink.harmonylink.repositories.user.userprofile;

import com.harmonylink.harmonylink.models.user.userprofile.Education;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EducationRepository extends MongoRepository<Education, String> {

    Education findByName(String name);

    @Query("{ 'name': { '$regex': '^?0', '$options': 'i'} }")
    List<Education> findEducationStartWith(String prefix, Pageable pageable);

}
