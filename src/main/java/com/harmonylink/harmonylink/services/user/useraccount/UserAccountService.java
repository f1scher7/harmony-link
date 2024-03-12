package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountValidator.isPasswordValid;
import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountValidator.isUserAtLeastSixteenYO;

@Service
public class UserAccountService {

    private final Logger USER_LOGIN_LOGGER = LoggerFactory.getLogger("UserLogin");
    private final Logger UPDATE_USER_DATA_LOGGER = LoggerFactory.getLogger("UserDataUpdate");

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void registerUserAccount(UserAccount userAccount) throws UserAlreadyExistsException, InvalidPasswordException, EmailAlreadyExistsException, InvalidGenderException, UserTooYoungException {
        if (userAccount == null) {
            throw new IllegalArgumentException("UserAccount can't be null");
        }

        if (this.userAccountRepository.findByLogin(userAccount.getLogin()) != null) {
            throw new UserAlreadyExistsException(userAccount.getLogin());
        }

        if (userAccount.getPassword() == null || !isPasswordValid(userAccount.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (userAccount.getEmail() == null || this.userAccountRepository.findByEmail(userAccount.getEmail()) != null) {
            throw new EmailAlreadyExistsException(userAccount.getEmail());
        }

        if (userAccount.getSex() == null || !userAccount.getSex().equals('M') && !userAccount.getSex().equals('K')) {
            throw new InvalidGenderException();
        }

        if (userAccount.getBirthdate() == null || !isUserAtLeastSixteenYO(userAccount.getBirthdate())) {
            throw new UserTooYoungException();
        }

        userAccount.setPassword(this.bCryptPasswordEncoder.encode(userAccount.getPassword()));
        this.userAccountRepository.save(userAccount);
    }

    public void loginUserAccount(String loginOrEmail, String password) throws InvalidCredentialsException {
        USER_LOGIN_LOGGER.info("Attempting to log in user with loginOrEmail: {}", loginOrEmail);

        UserAccount userAccount = this.userAccountRepository.findByLogin(loginOrEmail);

        if (userAccount == null) {
            USER_LOGIN_LOGGER.info("User not found by login {}, attempting to find by email: {}", loginOrEmail, loginOrEmail);
            userAccount = this.userAccountRepository.findByEmail(loginOrEmail);
        }

        if (userAccount != null && this.bCryptPasswordEncoder.matches(password, userAccount.getPassword())) {
            USER_LOGIN_LOGGER.info("User {} successfully logged in", userAccount.getLogin());
            //session.setAttribute("user", userAccount);
        } else  {
            USER_LOGIN_LOGGER.warn("Login failed for user: {}", loginOrEmail);
            throw new InvalidCredentialsException();
        }
    }



    public void updateUserData(String login, String newPassword, String newEmail) throws UserNotFoundException, InvalidPasswordException, EmailAlreadyExistsException {
        UserAccount existingAccount = this.userAccountRepository.findByLogin(login);
        if (existingAccount == null) {
            throw new UserNotFoundException();
        }

        if (newPassword != null) {
            if (!isPasswordValid(newPassword)) {
                throw new InvalidPasswordException();
            } else {
                existingAccount.setPassword(bCryptPasswordEncoder.encode(newPassword));
                UPDATE_USER_DATA_LOGGER.info("Password was updated successfully for user with login: {}", login);
            }
        }

        if (newEmail != null && !existingAccount.getEmail().equals(newEmail)) {
            if (this.userAccountRepository.findByEmail(newEmail) != null) {
                throw new EmailAlreadyExistsException(newEmail);
            } else {
                existingAccount.setEmail(newEmail);
                UPDATE_USER_DATA_LOGGER.info("Email was updated successfully for user with login: {}", login);
            }
        } else {
            throw new EmailAlreadyExistsException(newEmail);
        }

        this.userAccountRepository.save(existingAccount);
    }

    public void deleteUserAccountById(String id) {
        this.userAccountRepository.deleteById(id);
    }

}

