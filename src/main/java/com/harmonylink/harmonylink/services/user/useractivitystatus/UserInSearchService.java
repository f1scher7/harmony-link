package com.harmonylink.harmonylink.services.user.useractivitystatus;

import com.harmonylink.harmonylink.models.user.UserPreferencesFilter;
import com.harmonylink.harmonylink.models.user.UserSearchData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserInSearchService {

    private final ConcurrentHashMap<String, UserSearchData> inSearchUsers = new ConcurrentHashMap<>();

    public void addUserSearchData(UserProfile userProfile, UserPreferencesFilter userPreferencesFilter) {
        if (userProfile != null && userPreferencesFilter != null) {
            inSearchUsers.put(userProfile.getId(), new UserSearchData(userProfile, userPreferencesFilter));
        }
    }

    public void removeUserSearchData(String userProfileId) {
        inSearchUsers.remove(userProfileId);
    }

    public void updateUserSearchData(String userProfileId, UserProfile userProfile, UserPreferencesFilter userPreferencesFilter) {
        UserSearchData userSearchData = inSearchUsers.get(userProfileId);

        if (userSearchData != null) {
            userSearchData.setUserProfile(userProfile);
            userSearchData.setUserPreferencesFilter(userPreferencesFilter);
        }
    }

    public UserSearchData getUserSearchData(String userProfileId) {
        return inSearchUsers.get(userProfileId);
    }

    public List<UserSearchData> getAllInSearchUsers() {
        return new ArrayList<>(inSearchUsers.values());
    }

    public String displayInSearchUsers() {
        StringBuilder builder = new StringBuilder();
        inSearchUsers.forEach((key, value) -> builder
                .append("Id: ").append(key)
                .append("; User: ").append(value.toString())
                .append("\n"));
        return builder.toString();
    }

    public void clearAllInSearchUsers() {
        inSearchUsers.clear();
    }

}