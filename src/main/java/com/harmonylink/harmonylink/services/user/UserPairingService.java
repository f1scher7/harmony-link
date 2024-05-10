package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userdata.UserSearchData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.services.user.useractivitystatus.UserInCallPairService;
import com.harmonylink.harmonylink.services.user.useractivitystatus.UserInSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPairingService {

    private final UserInSearchService userInSearchService;
    private final UserInCallPairService userInCallPairService;


    @Autowired
    public UserPairingService(UserInSearchService userInSearchService, UserInCallPairService userInCallPairService) {
        this.userInSearchService = userInSearchService;
        this.userInCallPairService = userInCallPairService;
    }


    @Async
    @Scheduled(fixedRate = 5000)
    public void findUsersPairsForConnection() {
        List<UserSearchData> usersInSearch = this.userInSearchService.getAllInSearchUsers();

        usersInSearch.parallelStream().forEach(user1 -> {
            usersInSearch.stream()
                    .filter(user2 -> usersInSearch.indexOf(user2) > usersInSearch.indexOf(user1))
                    .forEach(user2 -> {
                        if (checkMatch(user1, user2)) {
                            this.userInCallPairService.addUserCallPairData(user1.getUserProfile(), user2.getUserProfile());
                            this.userInSearchService.removeUserSearchData(user1.getUserProfile().getId());
                            this.userInSearchService.removeUserSearchData(user2.getUserProfile().getId());
                        }
                    });
        });
    }

    public boolean checkMatch(UserSearchData userSearchData1, UserSearchData userSearchData2) {
        UserProfile userProfile1 = userSearchData1.getUserProfile();
        UserPreferencesFilter userPreferencesFilter1 = userSearchData1.getUserPreferencesFilter();

        UserProfile userProfile2 = userSearchData2.getUserProfile();
        UserPreferencesFilter userPreferencesFilter2 = userSearchData2.getUserPreferencesFilter();

        return isUserProfileMatchesUserPreferencesFilter(userProfile1, userPreferencesFilter2) && isUserProfileMatchesUserPreferencesFilter(userProfile2, userPreferencesFilter1);
    }

    public boolean isUserProfileMatchesUserPreferencesFilter(UserProfile userProfile, UserPreferencesFilter userPreferencesFilter) {
        if (!userPreferencesFilter.getCities().isEmpty() && !userPreferencesFilter.getCities().contains(userProfile.getCity())) {
            return false;
        }

        if (!userPreferencesFilter.getSex().isEmpty() && !userPreferencesFilter.getSex().equals(Character.toString(userProfile.getSex()))) {
            return false;
        }

        if (userPreferencesFilter.getAges().get(0) > userProfile.getAge() || userPreferencesFilter.getAges().get(1) < userProfile.getAge()) {
            return false;
        }

        if (userPreferencesFilter.getHeights().get(0) > userProfile.getHeight() || userPreferencesFilter.getHeights().get(1) < userProfile.getHeight()) {
            return false;
        }

        if (!userPreferencesFilter.getRelationshipStatus().isEmpty() && !userPreferencesFilter.getRelationshipStatus().equals(userProfile.getRelationshipStatus())) {
            return false;
        }

        if (!userPreferencesFilter.getHobbyIds().isEmpty() && !userPreferencesFilter.getHobbyIds().containsAll(userProfile.getHobbyIds())) {
            return false;
        }

        if (!userPreferencesFilter.getFieldsOfStudy().isEmpty() && !userPreferencesFilter.getFieldsOfStudy().contains(userProfile.getFieldOfStudy())) {
            return false;
        }

        return true;
    }

}
