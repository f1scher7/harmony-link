package com.harmonylink.harmonylink.services.user;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.harmonylink.harmonylink.services.user.UserAccountValidator.isPasswordValid;
import static com.harmonylink.harmonylink.services.user.UserAccountValidator.isUserAtLeastSixteenYO;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public UserAccount registerUserAccount(UserAccount userAccount) {
        if (this.userAccountRepository.findByLogin(userAccount.getLogin()) != null) {
            throw new RuntimeException("Login \"" + userAccount.getLogin() + "\" already exist.");
        }

        if (!isPasswordValid(userAccount.getPassword())) {
            throw new RuntimeException("The password must contain at least one lowercase and one uppercase letter, one number, and must be at least 8 characters long.");
        }

        if (this.userAccountRepository.findByEmail(userAccount.getEmail()) != null) {
            throw new RuntimeException("Email \"" + userAccount.getEmail() + "\" already exist.");
        }

        if (!userAccount.getSex().equals('M') && !userAccount.getSex().equals('K')) {
            throw new RuntimeException("There are 2 possible genders: Male or Female.");
        }

        if (!isUserAtLeastSixteenYO(userAccount.getBirthdate())) {
            throw new RuntimeException("The userAccount must be at least 16 years old.");
        }



        userAccount.setPassword(this.bCryptPasswordEncoder.encode(userAccount.getPassword()));
        return this.userAccountRepository.save(userAccount);
    }

    public void deleteUserAccountById(String id) {
        this.userAccountRepository.deleteById(id);
    }
    
}

