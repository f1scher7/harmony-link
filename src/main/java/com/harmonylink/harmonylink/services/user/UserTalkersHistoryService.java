package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserTalkersHistory;
import com.harmonylink.harmonylink.models.user.userdata.UserTalkerData;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserTalkersHistoryRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.useractivity.UserInCallPairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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


    @Transactional
    public void saveUserTalkersHistory(String userProfileId1) {
        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfileId1);
        if (userProfileOptional.isPresent()) {
            UserProfile userProfile1 = userProfileOptional.get();
            UserProfile userProfile2 = this.userInCallPairService.getAnotherUserProfileByUserProfileId(userProfileId1);
            if (userProfile2 != null) {
                UserTalkersHistory userTalkersHistory1 = this.userTalkersHistoryRepository.findByUserProfile(userProfile1);
                UserTalkersHistory userTalkersHistory2 = this.userTalkersHistoryRepository.findByUserProfile(userProfile2);

                List<UserTalkerData> talkersList1 = userTalkersHistory1.getTalkers();
                List<UserTalkerData> talkersList2 = userTalkersHistory2.getTalkers();

                if(talkersList1.size() >= 7) {
                    talkersList1.remove(0);
                }

                if(talkersList2.size() >= 7) {
                    talkersList2.remove(0);
                }

                talkersList1.add(new UserTalkerData(userProfile2));
                talkersList2.add(new UserTalkerData(userProfile1));

                userTalkersHistory1.setTalkers(talkersList1);
                userTalkersHistory2.setTalkers(talkersList2);

                this.userTalkersHistoryRepository.save(userTalkersHistory1);
                this.userTalkersHistoryRepository.save(userTalkersHistory2);
            }
        }
    }

}
