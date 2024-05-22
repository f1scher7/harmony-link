package com.harmonylink.harmonylink.services.user.useractivity;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.userdata.UserCallPairData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.UserProfileDoesntExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserInCallPairService {

    private final ConcurrentHashMap<String, UserCallPairData> inCallUsersPairs = new ConcurrentHashMap<>();
    private final UserActivityStatusService userActivityStatusService;


    @Autowired
    public UserInCallPairService(UserActivityStatusService userActivityStatusService) {
        this.userActivityStatusService = userActivityStatusService;
    }


    public String getUserProfilesId(UserProfile userProfile1, UserProfile userProfile2) {
        return userProfile1.getId() + userProfile2.getId();
    }

    public UserCallPairData getUserCallPairData(UserProfile userProfile1, UserProfile userProfile2) {
        if (userProfile1 != null && userProfile2 != null) {
            return inCallUsersPairs.get(getUserProfilesId(userProfile1, userProfile2));
        }

        return null;
    }

    public UserProfile getUserProfileByUserProfileId(String userProfileId) {
        for (UserCallPairData userCallPairData: inCallUsersPairs.values()) {
            if (userCallPairData.getUserProfile1().getId().equals(userProfileId)) {
                return userCallPairData.getUserProfile1();
            } else if (userCallPairData.getUserProfile2().getId().equals(userProfileId)) {
                return userCallPairData.getUserProfile2();
            }
        }

        return null;
    }

    public UserProfile getAnotherUserProfileByUserProfileId(String userProfileId) {
        for (UserCallPairData userCallPairData: inCallUsersPairs.values()) {
            if (userCallPairData.getUserProfile1().getId().equals(userProfileId)) {
                return userCallPairData.getUserProfile2();
            } else if (userCallPairData.getUserProfile2().getId().equals(userProfileId)) {
                return  userCallPairData.getUserProfile1();
            }
        }

        return null;
    }

    public List<UserCallPairData> getAllUserCallPairData() {
        return new ArrayList<>(inCallUsersPairs.values());
    }


    public void addUserCallPairData(UserProfile userProfile1, UserProfile userProfile2) {
        if (userProfile1 != null && userProfile2 != null) {
            inCallUsersPairs.put(getUserProfilesId(userProfile1, userProfile2), new UserCallPairData(userProfile1, userProfile2));
        }
    }

    public List<UserCallPairData> getAllUserCallInPairData() {
        return new ArrayList<>(inCallUsersPairs.values());
    }


    public void removeUserCallPairData(UserProfile userProfile1, UserProfile userProfile2) {
        if (userProfile1 != null && userProfile2 != null) {
            inCallUsersPairs.remove(getUserProfilesId(userProfile1, userProfile2));
        }
    }

    public void removeUserCallPairDataByUserProfileId(String userProfileId) throws UserProfileDoesntExistException {
        for (UserCallPairData userCallPairData : inCallUsersPairs.values()) {
            if (userCallPairData.getUserProfile1().getId().equals(userProfileId) || userCallPairData.getUserProfile2().getId().equals(userProfileId)) {
                removeUserCallPairData(userCallPairData.getUserProfile1(), userCallPairData.getUserProfile2());

                this.userActivityStatusService.updateUserActivityStatusInDB(userCallPairData.getUserProfile1().getId(), UserActivityStatusEnum.ONLINE);
                this.userActivityStatusService.updateUserActivityStatusInDB(userCallPairData.getUserProfile2().getId(), UserActivityStatusEnum.ONLINE);

                break;
            }
        }
    }

    public void removeAllUserCallPairData() {
        inCallUsersPairs.clear();
    }


    public String displayInSearchUsers() {
        StringBuilder builder = new StringBuilder();
        inCallUsersPairs.forEach((key, value) -> builder
                .append("Id: ").append(key)
                .append("; Pair: ").append(value.toString())
                .append("\n"));
        return builder.toString();
    }

}
