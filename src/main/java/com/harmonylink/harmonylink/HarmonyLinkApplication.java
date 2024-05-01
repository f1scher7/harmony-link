package com.harmonylink.harmonylink;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.token.VerificationTokenRepository;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.token.ResetPasswordTokenRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.CityRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.EducationRepository;
import com.harmonylink.harmonylink.repositories.user.userprofile.HobbyRepository;
import com.harmonylink.harmonylink.services.user.userprofile.UserProfileService;
import com.harmonylink.harmonylink.services.user.useraccount.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class HarmonyLinkApplication implements CommandLineRunner {

    private final Random random = new Random();

    private final UserAccountRepository userAccountRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final EducationRepository educationRepository;
    private final HobbyRepository hobbyRepository;
    private final CityRepository cityRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserProfileService userProfileService;
    private final RegistrationService registrationService;


    @Autowired
    public HarmonyLinkApplication(UserAccountRepository userAccountRepository, ResetPasswordTokenRepository resetPasswordTokenRepository, EducationRepository educationRepository, HobbyRepository hobbyRepository, CityRepository cityRepository, VerificationTokenRepository verificationTokenRepository, UserProfileService userProfileService, RegistrationService registrationService) {
        this.userAccountRepository = userAccountRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.educationRepository = educationRepository;
        this.hobbyRepository = hobbyRepository;
        this.cityRepository = cityRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userProfileService = userProfileService;
        this.registrationService = registrationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HarmonyLinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //Test registerUser func
        //UserAccount admin = new UserAccount(Role.ADMIN, "kaziof1scher", "Kazio1234", "maksymilian.fischer7@gmail.com", LocalDate.of(2000, 7, 8), 'M');
        //this.registrationService.registerNewUserAccount(admin);
        //VerificationToken verificationToken = this.verificationTokenRepository.findByUserAccount(admin);
        //this.registrationService.verifyNewUserAccount(verificationToken.getToken());

        //Test loginUser func
        //this.loginService.loginUserAccount("kaziof1scher", "Kazio1234");

        //Test resetUserAccountPassword
        //this.userAccountService.resetUserAccountPassword("maksymilian.fischer7@gmail.com");

        //Test setNewPasswordAfterReset;
        //UserAccount testUser = this.userAccountRepository.findByLogin("kaziof1scher");
        //ResetPasswordToken resetPasswordToken = this.resetPasswordTokenRepository.findByUserAccount(testUser);
        //this.userAccountService.setNewPasswordAfterReset(resetPasswordToken.getToken(), "Kazio1234");

        /*
        //Test for find funcs for Hobby and City
        Pageable pageable = createPageableWithLimit(5);
        List<City> cities = this.cityRepository.findCitiesStartingWith("kr", pageable);
        List<Hobby> hobbies = this.hobbyRepository.findHobbiesStartingWith("pływanie", pageable);

        System.out.println(cities);
        System.out.println();
        System.out.println(hobbies);
        */

        System.out.println();
        for (UserAccount userAccount : this.userAccountRepository.findAll()) {
            System.out.println(userAccount);
        }
        System.out.println();

    }

}
