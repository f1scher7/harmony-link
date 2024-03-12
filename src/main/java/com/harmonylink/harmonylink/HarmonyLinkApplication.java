package com.harmonylink.harmonylink;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.services.user.useraccount.UserAccountService;
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
    private final UserAccountService userAccountService;

    @Autowired
    public HarmonyLinkApplication(UserAccountRepository userAccountRepository, UserAccountService userAccountService) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountService = userAccountService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HarmonyLinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        /*
        Registration of test users
        for (int i = 1; i < 4; i++) {
            Character sex = random.nextBoolean() ? 'M' : 'K';
            this.userAccountService.registerUserAccount(new UserAccount("testuser" + i, "testUser" + i, "testuser" + i + "@gmail.com", LocalDate.of(2000, 7, 7), sex));
        }
        */

        //Test registerUser func
        //this.userAccountService.registerUserAccount(new UserAccount("kaziof1scher", "Kazio1234", "maks.f1scher@gmail.com", LocalDate.of(2000, 7, 7), 'M'));

        // Test loginUser func
        //this.userAccountService.loginUserAccount("maks.f1scher@gmail.com", "Kazio1234");

        //Test updateData func
        //this.userAccountService.updateUserData("testuser1", "4Qwe4q4w6e46q4w6e4", "qweqweji");

        System.out.println();
        for (UserAccount userAccount : this.userAccountRepository.findAll()) {
            System.out.println(userAccount);
        }

    }
}
