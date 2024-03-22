package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.user.tokens.VerificationToken;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.user.tokens.VerificationTokenRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.harmonylink.harmonylink.utils.UserAccountUtil.*;

@Service
public class RegistrationService {

    private final Logger UPDATE_USER_DATA_LOGGER = LoggerFactory.getLogger("UserDataUpdate");

    private final UserAccountRepository userAccountRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public RegistrationService(UserAccountRepository userAccountRepository, VerificationTokenRepository verificationTokenRepository, EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Transactional
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

        this.emailService.sendVerificationEmail(userAccount, verificationToken.getToken());
        this.verificationTokenRepository.save(verificationToken);
    }

    @Transactional
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

    public void deleteUserAccountByLogin(String login) {
        this.userAccountRepository.deleteUserAccountByLogin(login);
        UPDATE_USER_DATA_LOGGER.info("User with login: {} was deleted.", login);
    }

}
