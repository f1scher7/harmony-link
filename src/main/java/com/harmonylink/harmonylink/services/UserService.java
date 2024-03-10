package com.harmonylink.harmonylink.services;

import com.harmonylink.harmonylink.models.User;
import com.harmonylink.harmonylink.repositories.UserRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[0-9].*");
    }

    public boolean isUserAtLeastSixteenYO(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthdate, currentDate);
        int age = period.getYears();

        return age >= 16;
    }

    public User saveUser(User user) {
        if (this.userRepository.findByLogin(user.getLogin()) != null) {
            throw new RuntimeException("Login \"" + user.getLogin() + "\" already exist.");
        }

        if (!isPasswordValid(user.getPassword())) {
            throw new RuntimeException("The password must contain at least one lowercase and one uppercase letter, one number, and must be at least 8 characters long.");
        }

        if (this.userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email \"" + user.getEmail() + "\" already exist.");
        }

        if (!user.getSex().equals('M') && !user.getSex().equals('K')) {
            throw new RuntimeException("There are 2 possible genders: Male or Female.");
        }

        if (!isUserAtLeastSixteenYO(user.getBirthdate())) {
            throw new RuntimeException("The user must be at least 16 years old.");
        }



        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    public void deleteUserById(String id) {
        this.userRepository.deleteById(id);
    }

}

