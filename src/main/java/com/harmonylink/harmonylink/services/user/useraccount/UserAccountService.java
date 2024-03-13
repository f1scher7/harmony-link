package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountUtils.generatePasswordResetToken;
import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountValidator.isPasswordValid;
import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountValidator.isUserAtLeastSixteenYO;

@Service
public class UserAccountService {

    private final Logger USER_LOGIN_LOGGER = LoggerFactory.getLogger("UserLogin");
    private final Logger UPDATE_USER_DATA_LOGGER = LoggerFactory.getLogger("UserDataUpdate");

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender javaMailSender) {
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.javaMailSender = javaMailSender;
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

    public void logoutUserAccount(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();
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


    public void resetUserAccountPassword(String email) {
        UserAccount userAccount = this.userAccountRepository.findByEmail(email);
        if (userAccount == null) {
            throw new IllegalArgumentException("Email can't be null.");
        }

        userAccount.createPasswordResetToken();
        this.userAccountRepository.save(userAccount);

        SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
        String harmonyMail = System.getenv("HARMONYLINK_EMAIL");
        passwordResetEmail.setFrom(harmonyMail);
        passwordResetEmail.setTo(userAccount.getEmail());
        passwordResetEmail.setSubject("Resetowanie hasła");
        passwordResetEmail.setText("Aby zresetować swoje hasło, kliknij w link:\n\n" + "https://todo.com/reset-password?token=" + userAccount.getPasswordResetToken() + "\n\n" + "Jeśli nie prosiłeś o resetowanie hasła, zignoruj ten email lub skontaktuj się z pomocą techniczną, jeśli masz jakiekolwiek pytania.\n\n\n" + "Z poważaniem,\n" + "Twój HarmonyLink\n");

        this.javaMailSender.send(passwordResetEmail);
    }

    public void changeUserAccountPassword(String token, String newPassword) throws InvalidPasswordException {
        UserAccount userAccount = this.userAccountRepository.findByPasswordResetToken(token);
        if (userAccount == null) {
            throw new IllegalArgumentException("This token doesn't exist");
        }

        if (!isPasswordValid(newPassword)) {
            throw new InvalidPasswordException();
        }

        userAccount.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userAccount.setPasswordResetToken(null);
        userAccount.setPasswordResetTokenCreationDate(null);
        this.userAccountRepository.save(userAccount);
    }


    public void deleteUserAccountByLogin(String login) {
        this.userAccountRepository.deleteUserAccountByLogin(login);
        UPDATE_USER_DATA_LOGGER.info("User with login: {} was deleted.", login);
    }

}

