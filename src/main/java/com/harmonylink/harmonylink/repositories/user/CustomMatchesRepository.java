package com.harmonylink.harmonylink.repositories.user;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomMatchesRepository {

    private final MongoTemplate mongoTemplate;


    @Autowired
    public CustomMatchesRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public long countUserProfilesMatchesInSearch(String userProfileId, String sex, int minAge, int maxAge, int minHeight, int maxHeight, String relationshipStatus, List<String> cities, List<String> hobbyIds, List<String> studies) {
        Criteria criteria = new Criteria();

        if (sex != null && !sex.isEmpty()) {
            criteria.and("sex").is(sex);
        }

        if (relationshipStatus != null && !relationshipStatus.isEmpty()) {
            criteria.and("relationshipStatus").is(relationshipStatus);
        }

        if (cities != null && !cities.isEmpty()) {
            criteria.and("city").in(cities);
        }

        if (hobbyIds != null && !hobbyIds.isEmpty()) {
            criteria.and("hobbyIds").in(hobbyIds);
        }

        if (studies != null && !studies.isEmpty()) {
            criteria.and("fieldOfStudy").in(studies);
        }

        criteria.and("_id").ne(userProfileId);
        criteria.and("age").gte(minAge).lte(maxAge);
        criteria.and("height").gte(minHeight).lte(maxHeight);

        MatchOperation matchOperation = Aggregation.match(criteria);

        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("user_activity_statuses")
                .localField("_id")
                .foreignField("userProfile.$id")
                .as("userActivityStatuses");

        MatchOperation matchOperationActivityStatus = Aggregation.match(Criteria.where("userActivityStatuses.activityStatus").is("IN_SEARCH"));

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, lookupOperation, matchOperationActivityStatus);

        AggregationResults<UserProfile> results = this.mongoTemplate.aggregate(aggregation, "user_profiles", UserProfile.class);

        return results.getMappedResults().size();
    }

    public long countUserPreferencesFiltersMatchesInSearch(String userPreferencesFilterId, String sex, String relationshipStatus, String city) {
        Criteria criteria = new Criteria();

        criteria.and("_id").ne(userPreferencesFilterId);
        criteria.and("sex").is(sex);
        criteria.and("relationshipStatus").is(relationshipStatus);
        criteria.and("cities").in(city);

        MatchOperation matchOperation = Aggregation.match(criteria);

        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("user_activity_statuses")
                .localField("userProfile")
                .foreignField("userProfile")
                .as("userActivityStatuses");

        MatchOperation matchOperationUserActivityStatus = Aggregation.match(Criteria.where("userActivityStatuses.activityStatus").is("IN_SEARCH"));

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, lookupOperation, matchOperationUserActivityStatus);

        AggregationResults<UserPreferencesFilter> results = this.mongoTemplate.aggregate(aggregation, "user_preferences_filters", UserPreferencesFilter.class);

        return results.getMappedResults().size();
    }

}
