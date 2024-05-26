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
import com.harmonylink.harmonylink.services.user.UserTalkersHistoryService;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserNotFoundException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.UserTooYoungException;
import com.harmonylink.harmonylink.services.user.userprofile.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

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
    private final UserTalkersHistoryService userTalkersHistoryService;


    @Autowired
    public UserProfileService(UserAccountRepository userAccountRepository, UserProfileRepository userProfileRepository, EducationRepository educationRepository, CityRepository cityRepository, HobbyRepository hobbyRepository, UserPreferencesFilterService userPreferencesFilterService, UserTalkersHistoryService userTalkersHistoryService) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.educationRepository = educationRepository;
        this.cityRepository = cityRepository;
        this.hobbyRepository = hobbyRepository;
        this.userPreferencesFilterService = userPreferencesFilterService;
        this.userTalkersHistoryService = userTalkersHistoryService;
    }


    public void setOrUpdateUserProfileData(UserProfile userProfile, String userAccountId) throws UserNotFoundException, InvalidUserCityException, InvalidUserHeightException, InvalidRelationshipStatusException, InvalidUserHobbiesExceptions, InvalidUserFieldOfStudyException, UserTooYoungException {
        UserAccount userAccount = this.userAccountRepository.findById(userAccountId).orElseThrow(UserNotFoundException::new);

        Optional<UserProfile> userProfileOptional = this.userProfileRepository.findById(userProfile.getId());

        validateUserProfileData(userProfile, userAccount);

        if (userProfileOptional.isPresent()) {
            this.userProfileRepository.save(userProfile);
        } else {
            userProfile.setUserAccount(userAccount);

            this.userProfileRepository.save(userProfile);
            this.userPreferencesFilterService.saveDefaultUserPreferencesFilters(userProfile);
            this.userTalkersHistoryService.saveDefaultUserTalkersHistory(userProfile);
        }
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

    public void processUserProfileFromForm(ModelAndView modelAndView, UserProfile userProfile, UserAccount userAccount, String redirectPage, String errorPage) {
        modelAndView.addObject("userProfile", userProfile);
        modelAndView.addObject("sex", userProfile.getSex());
        modelAndView.addObject("relationshipStatus", userProfile.getRelationshipStatus());
        modelAndView.addObject("hobbies", getHobbies(userProfile.getHobbyIds()));

        try {
            setOrUpdateUserProfileData(userProfile, userAccount.getId());
            modelAndView.setViewName("redirect:/" + redirectPage);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvalidRelationshipStatusException e) {
            modelAndView.setViewName(errorPage);
            modelAndView.addObject("errorRelationship", e.getMessage());
        } catch (InvalidUserHeightException e) {
            modelAndView.setViewName(errorPage);
            modelAndView.addObject("errorHeight", e.getMessage());
        } catch (InvalidUserCityException e) {
            modelAndView.setViewName(errorPage);
            modelAndView.addObject("errorCity", e.getMessage());
        } catch (InvalidUserFieldOfStudyException e) {
            modelAndView.setViewName(errorPage);
            modelAndView.addObject("errorStudy", e.getMessage());
        } catch (InvalidUserHobbiesExceptions e) {
            modelAndView.setViewName(errorPage);
            modelAndView.addObject("errorHobbies", e.getMessage());
        } catch (UserTooYoungException e) {
            modelAndView.setViewName(errorPage);
            modelAndView.addObject("errorAge", e.getMessage());
        }
    }


    public UserProfile getUserProfileByAuthentication(Authentication authentication) {
        UserAccount userAccount = null;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");
            userAccount = this.userAccountRepository.findByEmail(email);
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            String username = authentication.getName();
            userAccount = this.userAccountRepository.findByLogin(username);
        }

        return this.userProfileRepository.findByUserAccount(userAccount);
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
