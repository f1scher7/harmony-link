package com.harmonylink.harmonylink;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.tokens.ResetPasswordTokenRepository;
import com.harmonylink.harmonylink.services.user.useraccount.LoginService;
import com.harmonylink.harmonylink.services.user.useraccount.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Random;

@SpringBootApplication
public class HarmonyLinkApplication implements CommandLineRunner {

    private final Random random = new Random();

    private final UserAccountRepository userAccountRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final RegistrationService registrationService;
    private final LoginService loginService;

    @Autowired
    public HarmonyLinkApplication(UserAccountRepository userAccountRepository, ResetPasswordTokenRepository resetPasswordTokenRepository, RegistrationService registrationService, LoginService loginService) {
        this.userAccountRepository = userAccountRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.registrationService = registrationService;
        this.loginService = loginService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HarmonyLinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //Test registerUser func
        //this.registrationService.registerNewUserAccount(new UserAccount("kaziof1scher7", "Kazio1234", "maks.fischer7@gmail.com", LocalDate.of(2000, 7, 7), 'M'));

        /*
        //Registration of test users
        for (int i = 1; i < 4; i++) {
            Character sex = random.nextBoolean() ? 'M' : 'K';
            this.registrationService.registerUserAccount(new UserAccount("testuser" + i, "testUser" + i, "testuser" + i + "@gmail.com", LocalDate.of(2000, 7, 7), sex));
        }
        */

        //Test loginUser func
        //this.loginService.loginUserAccount("kaziof1scher", "159753Fischer");

        //Test updateUserAccountData func
        //this.userAccountService.updateUserData("kaziof1scher", "Kazio1234", "Kazio7710", null);

        //Test resetUserAccountPassword
        //this.userAccountService.resetUserAccountPassword("maksymilian.fischer7@gmail.com");

        //Test setNewPasswordAfterReset;
        //UserAccount testUser = this.userAccountRepository.findByLogin("kaziof1scher");
        //ResetPasswordToken resetPasswordToken = this.resetPasswordTokenRepository.findByUserAccount(testUser);
        //this.userAccountService.setNewPasswordAfterReset(resetPasswordToken.getToken(), "Kazio1234");

        System.out.println();
        for (UserAccount userAccount : this.userAccountRepository.findAll()) {
            System.out.println(userAccount);
        }
        System.out.println();

    }
}
