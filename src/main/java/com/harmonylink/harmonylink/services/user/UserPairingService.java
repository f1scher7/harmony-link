package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userdata.UserSearchData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.services.realtime.WebRTCService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInCallPairService;
import com.harmonylink.harmonylink.services.user.useractivity.UserInSearchService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserPairingService {

    private final UserInSearchService userInSearchService;
    private final UserInCallPairService userInCallPairService;
    private final WebRTCService webRTCService;


    @Autowired
    public UserPairingService(UserInSearchService userInSearchService, UserInCallPairService userInCallPairService, WebRTCService webRTCService) {
        this.userInSearchService = userInSearchService;
        this.userInCallPairService = userInCallPairService;
        this.webRTCService = webRTCService;
    }


    @Async
    @Scheduled(fixedRate = 3000)
    public void findUsersPairsForConnection() {
        List <UserSearchData> usersInSearch = this.userInSearchService.getAllInSearchUsers();
        int pageSize = 50;

        if (usersInSearch.size() >= 2) {
            for (int page = 0; page * pageSize < usersInSearch.size(); page++) {
                int start = page * pageSize;
                int end = Math.min((page + 1) * pageSize, usersInSearch.size());

                List<UserSearchData> usersPage = usersInSearch.subList(start, end);

                processUsersPage(usersPage);
            }
        }
    }

    private void processUsersPage(List<UserSearchData> usersPage) {
        for (int i = 0; i < usersPage.size(); i++) {
            UserSearchData userSearchData1 = usersPage.get(i);

            for (int j = i + 1; j < usersPage.size(); j++) {
                UserSearchData userSearchData2 = usersPage.get(j);

                if (checkMatch(userSearchData1, userSearchData2)) {
                    this.userInCallPairService.addUserCallPairData(userSearchData1.getUserProfile(), userSearchData2.getUserProfile());
                    this.userInSearchService.removeUserSearchData(userSearchData1.getUserProfile().getId());
                    this.userInSearchService.removeUserSearchData(userSearchData2.getUserProfile().getId());

                    try {
                        this.webRTCService.initiateConnection(userSearchData1.getUserProfile());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }
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
