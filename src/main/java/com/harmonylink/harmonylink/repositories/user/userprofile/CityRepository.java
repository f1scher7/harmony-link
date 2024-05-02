package com.harmonylink.harmonylink.repositories.user.userprofile;

import com.harmonylink.harmonylink.models.user.userprofile.City;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends MongoRepository<City, String> {

    City findByName(String name);

    @Query("{ 'name': { '$regex': '^?0', '$options': 'i' } }")
    List<City> findCitiesStartingWith(String prefix, Pageable pageable);

}
