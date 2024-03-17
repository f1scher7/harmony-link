package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.tokens.ResetPasswordToken;
import com.harmonylink.harmonylink.models.user.tokens.VerificationToken;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.tokens.ResetPasswordTokenRepository;
import com.harmonylink.harmonylink.repositories.user.tokens.VerificationTokenRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountUtils.*;
import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountValidator.isPasswordValid;
import static com.harmonylink.harmonylink.services.user.useraccount.UserAccountValidator.isUserAtLeastSixteenYO;

@Service
public class UserAccountService {

    private final Logger USER_LOGIN_LOGGER = LoggerFactory.getLogger("UserLogin");
    private final Logger UPDATE_USER_DATA_LOGGER = LoggerFactory.getLogger("UserDataUpdate");

    private final UserAccountRepository userAccountRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JavaMailSender javaMailSender;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository, VerificationTokenRepository verificationTokenRepository, ResetPasswordTokenRepository resetPasswordTokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender javaMailSender) {
        this.userAccountRepository = userAccountRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.javaMailSender = javaMailSender;
    }


    public void registerNewUserAccount(UserAccount userAccount) throws UserAlreadyExistsException, InvalidPasswordException, EmailAlreadyExistsException, EmailNotFoundException, UserTooYoungException {
        if (userAccount == null) {
            throw new IllegalArgumentException("UserAccount can't be null");
        }

        if (this.userAccountRepository.findByLogin(userAccount.getLogin()) != null || userAccount.getLogin().isEmpty()) {
            throw new UserAlreadyExistsException(userAccount.getLogin());
        }

        if (userAccount.getPassword() == null || !isPasswordValid(userAccount.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (userAccount.getEmail() == null || this.userAccountRepository.findByEmail(userAccount.getEmail()) != null || userAccount.getEmail().isEmpty()) {
            throw new EmailAlreadyExistsException(userAccount.getEmail());
        }

        if (userAccount.getBirthdate() == null || !isUserAtLeastSixteenYO(userAccount.getBirthdate())) {
            throw new UserTooYoungException();
        }

        userAccount.setPassword(this.bCryptPasswordEncoder.encode(userAccount.getPassword()));

        String token = generateToken();
        VerificationToken verificationToken = new VerificationToken(token, userAccount);

        String verificationLink = "http://3.70.228.63/api/users/verify-account?token=" + token;
        String subject = "Weryfikacja maila";
        String text = "Cześć,\n\n"
                + "Dziękujemy za utworzenie konta! Jesteśmy podekscytowani, że do nas dołączyłeś.\n\n"
                + "Aby aktywować swoje konto, musisz potwierdzić swój adres e-mail. Kliknij poniższy link, aby to zrobić:\n\n"
                + verificationLink + "\n\n"
                + "Jeśli nie utworzyłeś konta i otrzymałeś ten e-mail przez pomyłkę, zignoruj go.\n\n"
                + "Dziękujemy,\n"
                + "Twój HarmonyLink";

        try {
            this.javaMailSender.send(mailGenerator(userAccount.getEmail(), subject, text));
            this.verificationTokenRepository.save(verificationToken);
        } catch (MailSendException e) {
            throw new EmailNotFoundException();
        }
    }

    public void verifyNewUserAccount(String token, HttpServletRequest request) throws InvalidTokenException {
        VerificationToken verificationToken = this.verificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            throw new InvalidTokenException();
        }

        UserAccount userAccount = verificationToken.getUserAccount();

        if (userAccount == null) {
            throw new InvalidTokenException();
        }

        userAccount.addIpAddress(updateUserIpAddress(request));

        this.userAccountRepository.save(userAccount);
        this.verificationTokenRepository.delete(verificationToken);
    }


    public void loginUserAccount(String loginOrEmail, String password, HttpSession session) throws InvalidCredentialsException {
        USER_LOGIN_LOGGER.info("Attempting to log in user with loginOrEmail: {}", loginOrEmail);

        UserAccount userAccount = this.userAccountRepository.findByLogin(loginOrEmail);

        if (userAccount == null) {
            USER_LOGIN_LOGGER.info("User not found by login {}, attempting to find by email: {}", loginOrEmail, loginOrEmail);
            userAccount = this.userAccountRepository.findByEmail(loginOrEmail);
        }

        if (userAccount != null && this.bCryptPasswordEncoder.matches(password, userAccount.getPassword())) {
            USER_LOGIN_LOGGER.info("User {} successfully logged in", userAccount.getLogin());
            session.setAttribute("user", userAccount);
        } else  {
            USER_LOGIN_LOGGER.warn("Login failed for user: {}", loginOrEmail);
            throw new InvalidCredentialsException();
        }
    }

    public void logoutUserAccount(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();
    }


    public void updateUserData(String login, String oldPassword, String newPassword, String newEmail) throws UserNotFoundException, InvalidPasswordException, WrongPasswordException, EmailAlreadyExistsException {
        UserAccount existingAccount = this.userAccountRepository.findByLogin(login);

        if (existingAccount == null) {
            throw new UserNotFoundException();
        }

        if (newPassword != null) {
            if (bCryptPasswordEncoder.matches(oldPassword, existingAccount.getPassword())) {
                if (!isPasswordValid(newPassword)) {
                    throw new InvalidPasswordException();
                } else {
                    existingAccount.setPassword(bCryptPasswordEncoder.encode(newPassword));
                    UPDATE_USER_DATA_LOGGER.info("Password was updated successfully for user with login: {}", login);
                }
            } else {
                throw new WrongPasswordException();
            }
        }

        if (newEmail != null) {
            if (!existingAccount.getEmail().equals(newEmail) && bCryptPasswordEncoder.matches(oldPassword, existingAccount.getPassword())) {
                if (this.userAccountRepository.findByEmail(newEmail) != null) {
                    throw new EmailAlreadyExistsException(newEmail);
                } else {
                    existingAccount.setEmail(newEmail);
                    UPDATE_USER_DATA_LOGGER.info("Email was updated successfully for user with login: {}", login);
                }
            } else {
                throw new WrongPasswordException();
            }
        }

        this.userAccountRepository.save(existingAccount);
    }


    public void resetUserAccountPassword(String email) throws EmailNotFoundException {
        UserAccount userAccount = this.userAccountRepository.findByEmail(email);

        if (userAccount == null) {
            throw new EmailNotFoundException();
        }

        String token = generateToken();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, userAccount);

        String setPasswordLink = "http://3.70.228.63/api/users/set-password?token=" + token;
        String subject = "Resetowanie hasła";
        String text = "Cześć,\n\n"
                +"Aby zresetować swoje hasło, kliknij w link:\n\n"
                + setPasswordLink
                + "\n\n" + "Jeśli nie prosiłeś o resetowanie hasła, zignoruj ten email lub skontaktuj się z pomocą techniczną, jeśli masz jakiekolwiek pytania.\n\n"
                + "Z poważaniem,\n" + "Twój HarmonyLink\n";

        try {
            this.javaMailSender.send(mailGenerator(userAccount.getEmail(), subject, text));
            this.resetPasswordTokenRepository.save(resetPasswordToken);
        } catch (MailSendException e) {
            throw new EmailNotFoundException();
        }
    }

    public void setNewPasswordAfterReset(String token, String newPassword, String confirmPassword) throws InvalidTokenException, InvalidPasswordException, PasswordsMatchingException {
        ResetPasswordToken resetPasswordToken = this.resetPasswordTokenRepository.findByToken(token);

        if (resetPasswordToken == null) {
            throw new InvalidTokenException();
        }

        UserAccount userAccount = resetPasswordToken.getUserAccount();

        if (!isPasswordValid(newPassword)) {
            throw new InvalidPasswordException();
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordsMatchingException();
        }

        userAccount.setPassword(bCryptPasswordEncoder.encode(newPassword));
        this.userAccountRepository.save(userAccount);
        this.resetPasswordTokenRepository.delete(resetPasswordToken);
    }


    public void deleteUserAccountByLogin(String login) {
        this.userAccountRepository.deleteUserAccountByLogin(login);
        UPDATE_USER_DATA_LOGGER.info("User with login: {} was deleted.", login);
    }

}

