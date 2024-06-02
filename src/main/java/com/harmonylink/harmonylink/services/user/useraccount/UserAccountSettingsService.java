package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.harmonylink.harmonylink.utils.UserUtil.isPasswordValid;

@Service
public class UserAccountSettingsService {

    private final Logger UPDATE_USER_DATA_LOGGER = LoggerFactory.getLogger("UserDataUpdate");

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserAccountSettingsService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void changePassword(String login, String oldPassword, String newPassword, String confirmPassword) throws UserNotFoundException, InvalidPasswordException, WrongPasswordException, PasswordsMatchingException {
        UserAccount userAccount = this.userAccountRepository.findByLogin(login);

        if (userAccount == null) {
            throw new UserNotFoundException();
        }

        if (this.passwordEncoder.matches(oldPassword, userAccount.getPassword())) {

            if (isPasswordValid(newPassword)) {

                if (newPassword.equals(confirmPassword)) {
                    userAccount.setPassword(this.passwordEncoder.encode(newPassword));
                    this.userAccountRepository.save(userAccount);

                    UPDATE_USER_DATA_LOGGER.info("Password was updated successfully for user with login: " + login + ". New password: " + newPassword);
                } else {
                    throw new PasswordsMatchingException();
                }

            } else {
                throw new InvalidPasswordException();
            }

        } else {
            throw new WrongPasswordException();
        }
    }

    public void changeEmail(String login, String newEmail) {

    }

}
