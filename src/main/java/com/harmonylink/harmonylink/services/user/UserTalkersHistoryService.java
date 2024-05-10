package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserTalkersHistory;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserTalkersHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTalkersHistoryService {

    private final UserTalkersHistoryRepository userTalkersHistoryRepository;


    @Autowired
    public UserTalkersHistoryService(UserTalkersHistoryRepository userTalkersHistoryRepository) {
        this.userTalkersHistoryRepository = userTalkersHistoryRepository;
    }


    public void saveDefaultUserTalkersHistory(UserProfile userProfile) {
        UserTalkersHistory userTalkersHistory = new UserTalkersHistory();

        userTalkersHistory.setUserProfile(userProfile);

        this.userTalkersHistoryRepository.save(userTalkersHistory);
    }

}
