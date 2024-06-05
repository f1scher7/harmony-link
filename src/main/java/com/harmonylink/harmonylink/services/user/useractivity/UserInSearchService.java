package com.harmonylink.harmonylink.services.user.useractivity;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.userdata.UserSearchData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserPreferencesFilterRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.UserProfileDoesntExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserInSearchService {

    private final ConcurrentHashMap<String, UserSearchData> inSearchUsers = new ConcurrentHashMap<>();
    private final UserActivityStatusService userActivityStatusService;
    private final UserProfileRepository userProfileRepository;
    private final UserPreferencesFilterRepository userPreferencesFilterRepository;


    @Autowired
    public UserInSearchService(UserActivityStatusService userActivityStatusService, UserProfileRepository userProfileRepository, UserPreferencesFilterRepository userPreferencesFilterRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userActivityStatusService = userActivityStatusService;
        this.userPreferencesFilterRepository = userPreferencesFilterRepository;
    }


    public UserSearchData getUserSearchData(String userProfileId) {
        if (userProfileId != null) {
            return inSearchUsers.get(userProfileId);
        }

        return null;
    }

    public List<UserSearchData> getAllInSearchUsers() {
        return new ArrayList<>(inSearchUsers.values());
    }


    public void addUserSearchData(String userProfileId) throws UserProfileDoesntExistException {
        this.userActivityStatusService.updateUserActivityStatusInDB(userProfileId, UserActivityStatusEnum.IN_SEARCH);

        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId);
        UserProfile userProfile = null;

        if (userProfileOptional.isPresent()) {
            userProfile = userProfileOptional.get();
        }

        UserPreferencesFilter userPreferencesFilter = this.userPreferencesFilterRepository.findByUserProfile(userProfile);

        if (userProfile != null && userPreferencesFilter != null) {
            inSearchUsers.put(userProfile.getId(), new UserSearchData(userProfile, userPreferencesFilter));
        }
    }


    public void removeUserSearchData(String userProfileId) {
        if (userProfileId != null) {
            inSearchUsers.remove(userProfileId);
        }
    }

    public void removeAllInSearchUsers() {
        inSearchUsers.clear();
    }


    public String displayInSearchUsers() {
        StringBuilder builder = new StringBuilder();
        inSearchUsers.forEach((key, value) -> builder
                .append("Id: ").append(key)
                .append("; User: ").append(value.toString())
                .append("\n"));
        return builder.toString();
    }

}
