package com.harmonylink.harmonylink.services.user.userprofile;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.userprofile.Hobby;
import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.CityRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.EducationRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.HobbyRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.UserProfileRepository;
import com.harmonylink.harmonylink.services.user.UserPreferencesFilterService;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserNotFoundException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserTooYoungException;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.harmonylink.harmonylink.utils.UserUtil.getUserAge;

@Service
public class UserProfileService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final EducationRepository educationRepository;
    private final CityRepository cityRepository;
    private final HobbyRepository hobbyRepository;
    private final UserPreferencesFilterService userPreferencesFilterService;


    @Autowired
    public UserProfileService(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository, EducationRepository educationRepository, CityRepository cityRepository, HobbyRepository hobbyRepository, UserPreferencesFilterService userPreferencesFilterService) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.educationRepository = educationRepository;
        this.cityRepository = cityRepository;
        this.hobbyRepository = hobbyRepository;
        this.userPreferencesFilterService = userPreferencesFilterService;
    }


    @Transactional
    public void setOrUpdateUserProfileData(UserProfile userProfile, String userAccountId) throws UserNotFoundException, InvalidUserCityException, InvalidUserHeightException, InvalidRelationshipStatusException, InvalidUserHobbiesExceptions, InvalidUserFieldOfStudyException, UserTooYoungException {
        UserAccount userAccount = this.userAccountRepository.findById(userAccountId).orElseThrow(UserNotFoundException::new);

        validateUserProfileData(userProfile, userAccount);

        userProfile.setUserAccount(userAccount);
        this.userProfileRepository.save(userProfile);
        this.userPreferencesFilterService.saveDefaultUserPreferencesFilters(userProfile);
    }

    private void validateUserProfileData(UserProfile userProfile, UserAccount userAccount) throws InvalidUserCityException, InvalidUserHeightException, InvalidRelationshipStatusException, InvalidUserHobbiesExceptions, InvalidUserFieldOfStudyException, UserTooYoungException {
        if (userProfile.getRelationshipStatus() == null) {
            throw new InvalidRelationshipStatusException();
        }

        if (userProfile.getHeight() < 150 || userProfile.getHeight() > 230) {
            throw new InvalidUserHeightException();
        }

        if (userProfile.getCity() == null || this.cityRepository.findByName(userProfile.getCity()) == null) {
            throw new InvalidUserCityException();
        }

        if (userProfile.getFieldOfStudy() == null || this.educationRepository.findByName(userProfile.getFieldOfStudy()) == null) {
            throw new InvalidUserFieldOfStudyException();
        }

        if (userProfile.getHobbyIds() == null || userProfile.getHobbyIds().isEmpty()) {
            throw new InvalidUserHobbiesExceptions();
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


    public List<String> getHobbyIds(List<String> hobbyValue) {
        List<String> hobbyIds = new ArrayList<>();

        for (String string : hobbyValue) {
            Hobby hobby = this.hobbyRepository.findByName(string);
            hobbyIds.add(hobby.getId());
        }

        return hobbyIds;
    }

    public List<String> getHobbies(List<String> hobbyIds) {
        List<String> hobbies = new ArrayList<>();

        for (String string : hobbyIds) {
            Optional<Hobby> hobbyOptional = this.hobbyRepository.findById(string);
            hobbyOptional.ifPresent(hobby -> hobbies.add(hobby.getName()));
        }

        return hobbies;
    }

}
