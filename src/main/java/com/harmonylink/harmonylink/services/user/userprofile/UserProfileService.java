package com.harmonylink.harmonylink.services.user.userprofile;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.CityRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserNotFoundException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserTooYoungException;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.harmonylink.harmonylink.utils.UserUtil.getUserAge;

@Service
public class UserProfileService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    public final CityRepository cityRepository;


    @Autowired
    public UserProfileService(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository, CityRepository cityRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.cityRepository = cityRepository;
    }


    @Transactional
    public void setOrUpdateUserProfileData(UserProfile userProfile, String userAccountId) throws UserNotFoundException, InvalidUserCityException, InvalidUserHeightException, InvalidRelationshipStatusException, InvalidUserHobbiesExceptions, InvalidUserFieldOfStudyException, UserTooYoungException {
        UserAccount userAccount = this.userAccountRepository.findById(userAccountId).orElseThrow(UserNotFoundException::new);

        validateUserProfileData(userProfile, userAccount);

        userProfile.setUserAccount(userAccount);

        this.userProfileRepository.save(userProfile);
    }

    private void validateUserProfileData(UserProfile userProfile, UserAccount userAccount) throws InvalidUserCityException, InvalidUserHeightException, InvalidRelationshipStatusException, InvalidUserHobbiesExceptions, InvalidUserFieldOfStudyException, UserTooYoungException {
        if (userProfile.getCity() == null || this.cityRepository.findByName(userProfile.getCity()) != null) {
            throw new InvalidUserCityException();
        }

        if (userProfile.getHeight() < 150 || userProfile.getHeight() > 230) {
            throw new InvalidUserHeightException();
        }

        if (userProfile.getRelationshipStatus() == null) {
            throw new InvalidRelationshipStatusException();
        }

        if (userProfile.getHobbyIds().isEmpty()) {
            throw new InvalidUserHobbiesExceptions();
        }

        if (userProfile.getFieldOfStudy() == null || userProfile.getFieldOfStudy().length() < 5) {
            throw new InvalidUserFieldOfStudyException();
        }

        if (userAccount.getGoogleId() == null) {
            userProfile.setNickname(userAccount.getLogin());
            userProfile.setAge(getUserAge(userAccount.getBirthdate()));
            userProfile.setSex(userAccount.getSex());
        } else {
            if (userProfile.getAge() < 16) {
                throw new UserTooYoungException();
            }
            userProfile.setNickname(userAccount.getEmail().substring(0, userAccount.getEmail().indexOf('@')));
        }
    }

}
