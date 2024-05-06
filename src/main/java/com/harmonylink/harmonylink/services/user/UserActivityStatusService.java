package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.enums.UserActivityStatusEnum;
import com.harmonylink.harmonylink.models.user.UserActivityStatus;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserActivityStatusRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.UserProfileDoesntExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

@Service
public class UserActivityStatusService {

    private final UserProfileRepository userProfileRepository;
    private final UserActivityStatusRepository userActivityStatusRepository;


    @Autowired
    public UserActivityStatusService(UserProfileRepository userProfileRepository, UserActivityStatusRepository userActivityStatusRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userActivityStatusRepository = userActivityStatusRepository;
    }


    public String retrieveUserIdFromWebSocketSession(WebSocketSession webSocketSession) {
        return webSocketSession.getAttributes().get("userProfileId").toString();
    }

    public UserActivityStatus getUserActivityStatusByUserProfileId(String userProfileId) {
        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId);

        if (userProfileOptional.isPresent()) {
           UserProfile userProfile = userProfileOptional.get();

           return this.userActivityStatusRepository.findByUserProfile(userProfile);
        }

        return null;
    }

    public void updateUserActivityStatus(String userProfileId, UserActivityStatusEnum userActivityStatusEnum) throws UserProfileDoesntExistException {
        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId);

        if (userProfileOptional.isPresent()) {
            UserProfile userProfile = userProfileOptional.get();

            UserActivityStatus userActivityStatus = this.userActivityStatusRepository.findByUserProfile(userProfile);

            if (userActivityStatus != null) {
                userActivityStatus.setUserActivityStatus(userActivityStatusEnum);
            } else {
                userActivityStatus = new UserActivityStatus();
                userActivityStatus.setUserProfile(userProfile);
                userActivityStatus.setUserActivityStatus(userActivityStatusEnum);
            }

            this.userActivityStatusRepository.save(userActivityStatus);
        } else {
            throw new UserProfileDoesntExistException(userProfileId);
        }
    }

}
