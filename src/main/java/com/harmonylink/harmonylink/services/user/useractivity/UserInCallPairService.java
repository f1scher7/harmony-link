package com.harmonylink.harmonylink.services.user.useractivitystatus;

import com.harmonylink.harmonylink.models.user.userdata.UserCallPairData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.harmonylink.harmonylink.utils.UserUtil.getUserProfilesId;

@Service
public class UserInCallPairService {

    private final ConcurrentHashMap<String, UserCallPairData> inCallUsersPairs = new ConcurrentHashMap<>();

    public void addUserCallPairData(UserProfile userProfile1, UserProfile userProfile2) {
        if (userProfile1 != null && userProfile2 != null) {
            inCallUsersPairs.put(getUserProfilesId(userProfile1, userProfile2), new UserCallPairData(userProfile1, userProfile2));
        }
    }

    public UserCallPairData getUserCallPairData(UserProfile userProfile1, UserProfile userProfile2) {
        return inCallUsersPairs.get(getUserProfilesId(userProfile1, userProfile2));
    }

    public List<UserCallPairData> getAllUserCallInPairData() {
        return new ArrayList<>(inCallUsersPairs.values());
    }

    public void removeUserCallPairData(UserProfile userProfile1, UserProfile userProfile2) {
        inCallUsersPairs.remove(getUserProfilesId(userProfile1, userProfile2));
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
