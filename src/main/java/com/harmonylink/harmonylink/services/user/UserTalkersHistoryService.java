package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserTalkersHistory;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserTalkersHistoryRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.useractivity.UserInCallPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserTalkersHistoryService {

    private final UserInCallPairService userInCallPairService;
    private final UserTalkersHistoryRepository userTalkersHistoryRepository;
    private final UserProfileRepository userProfileRepository;


    @Autowired
    public UserTalkersHistoryService(UserInCallPairService userInCallPairService, UserTalkersHistoryRepository userTalkersHistoryRepository, UserProfileRepository userProfileRepository) {
        this.userInCallPairService = userInCallPairService;
        this.userTalkersHistoryRepository = userTalkersHistoryRepository;
        this.userProfileRepository = userProfileRepository;
    }


    public void saveDefaultUserTalkersHistory(UserProfile userProfile) {
        UserTalkersHistory userTalkersHistory = new UserTalkersHistory();

        userTalkersHistory.setUserProfile(userProfile);

        this.userTalkersHistoryRepository.save(userTalkersHistory);
    }

    public void saveUserTalkersHistory(String userProfileId1) {
        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId1);
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile1 = userProfileOptional.get();

            UserProfile userProfile2 = this.userInCallPairService.getAnotherUserProfileByUserProfileId(userProfileId1);
            if (userProfile2 != null) {
                UserTalkersHistory userTalkersHistory1 = this.userTalkersHistoryRepository.findByUserProfile(userProfile1);
                UserTalkersHistory userTalkersHistory2 = this.userTalkersHistoryRepository.findByUserProfile(userProfile2);

                userTalkersHistory1.addTalker(userProfile2);
                userTalkersHistory2.addTalker(userProfile1);

                this.userTalkersHistoryRepository.save(userTalkersHistory1);
                this.userTalkersHistoryRepository.save(userTalkersHistory2);
            }
        }
    }

}
