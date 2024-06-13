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


    public void setOrUpdateUserProfileData(UserProfile userProfile, String userAccountId) throws UserNotFoundException, InvalidUserCityException, InvalidUserHeightException, InvalidRelationshipStatusException, InvalidUserHobbiesExceptions, InvalidUserFieldOfStudyException, UserTooYoungException {
        UserAccount userAccount = this.userAccountRepository.findById(userAccountId).orElseThrow(UserNotFoundException::new);

        validateUserProfileData(userProfile, userAccount);

        UserProfile userProfileOptional = this.userProfileRepository.findByUserAccount(userAccount);

        if (userProfileOptional != null) {
            userProfileOptional.setAge(userProfile.getAge());
            userProfileOptional.setCity(userProfile.getCity());
            userProfileOptional.setRelationshipStatus(userProfile.getRelationshipStatus());
            userProfileOptional.setFieldOfStudy(userProfile.getFieldOfStudy());
            userProfileOptional.setHobbyIds(userProfile.getHobbyIds());

            this.userProfileRepository.save(userProfileOptional);
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

            if (this.userProfileRepository.findByUserAccount(userAccount) == null) {
                userProfile.setAge(getUserAge(userAccount.getBirthdate()));
            }

            userProfile.setSex(userAccount.getSex());
        } else {
            if (userProfile.getAge() < 16) {
                throw new UserTooYoungException();
            }

            userProfile.setNickname(userAccount.getEmail().substring(0, userAccount.getEmail().indexOf('@')));
        }
    }

    public void processUserProfileFromForm(ModelAndView modelAndView, UserProfile userProfile, UserAccount userAccount, String redirectPage, String errorPage) {
        modelAndView.addObject("sex", userProfile.getSex());
        modelAndView.addObject("relationshipStatus", userProfile.getRelationshipStatus());

        try {
            modelAndView.addObject("hobbies", getHobbies(userProfile.getHobbyIds()));
            setOrUpdateUserProfileData(userProfile, userAccount.getId());
            modelAndView.setViewName("redirect:/" + redirectPage);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvalidRelationshipStatusException e) {
            modelAndView.addObject("errorRelationship", e.getMessage());
            modelAndView.setViewName(errorPage);
        } catch (InvalidUserHeightException e) {
            modelAndView.addObject("errorHeight", e.getMessage());
            modelAndView.setViewName(errorPage);
        } catch (InvalidUserCityException e) {
            modelAndView.addObject("errorCity", e.getMessage());
            modelAndView.setViewName(errorPage);
        } catch (InvalidUserFieldOfStudyException e) {
            modelAndView.addObject("errorStudy", e.getMessage());
            modelAndView.setViewName(errorPage);
        } catch (InvalidUserHobbiesExceptions e) {
            modelAndView.addObject("errorHobbies", e.getMessage());
            modelAndView.setViewName(errorPage);
        } catch (UserTooYoungException e) {
            modelAndView.addObject("errorAge", e.getMessage());
            modelAndView.setViewName(errorPage);
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

    public List<String> getHobbies(List<String> hobbyIds) throws InvalidUserHobbiesExceptions {
        List<String> hobbies = new ArrayList<>();
        if (hobbyIds == null) {
            throw new InvalidUserHobbiesExceptions();
        } else {
            for (String string : hobbyIds) {
                Optional<Hobby> hobbyOptional = this.hobbyRepository.findById(string);
                hobbyOptional.ifPresent(hobby -> hobbies.add(hobby.getName()));
            }

            return hobbies;
        }
    }

}
