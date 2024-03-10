package com.harmonylink.harmonylink;

import com.harmonylink.harmonylink.models.User;
import com.harmonylink.harmonylink.repositories.UserRepository;
import com.harmonylink.harmonylink.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Random;

@SpringBootApplication
public class HarmonyLinkApplication implements CommandLineRunner {

    private Random random = new Random();

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public HarmonyLinkApplication(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HarmonyLinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        this.userService.saveUser(new User("kaziofiqwescher", "Kazio4564", "Maks Fischer", "maks.f1scher@gmail.com", LocalDate.of(2000, 7, 7), 'M'));

        /*
        for (int i = 1; i < 4; i++) {
            Character sex = random.nextBoolean() ? 'M' : 'K';
            this.userService.saveUser(new User("testuser" + i, "testuser" + i, "Test User " + i, "testuser" + i + "@gmail.com", LocalDate.of(2000, 7, 7), sex));
        }
        */

        for (User user: this.userRepository.findAll()) {
            System.out.println(user);
        }

    }
}
