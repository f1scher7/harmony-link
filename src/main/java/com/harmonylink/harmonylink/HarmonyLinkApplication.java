package com.harmonylink.harmonylink;

import com.harmonylink.harmonylink.enums.Role;
import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.token.VerificationTokenRepository;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.token.ResetPasswordTokenRepository;
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
    private final VerificationTokenRepository verificationTokenRepository;
    private final RegistrationService registrationService;

    @Autowired
    public HarmonyLinkApplication(UserAccountRepository userAccountRepository, ResetPasswordTokenRepository resetPasswordTokenRepository, VerificationTokenRepository verificationTokenRepository, RegistrationService registrationService) {
        this.userAccountRepository = userAccountRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.registrationService = registrationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HarmonyLinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //Test registerUser func
        //this.registrationService.registerNewUserAccount(new UserAccount(Role.USER, "testuser1", "testUser1", "temp.email7722@gmail.com", LocalDate.of(2001, 7, 7), 'M'));
        //this.registrationService.verifyNewUserAccount("");

        //Test loginUser func
        //this.loginService.loginUserAccount("kaziof1scher", "Kazio1234");

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
