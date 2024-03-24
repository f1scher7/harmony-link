package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.token.VerificationToken;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.token.VerificationTokenRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.harmonylink.harmonylink.utils.UserAccountUtil.*;

@Service
public class RegistrationService {

    private final UserAccountRepository userAccountRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public RegistrationService(UserAccountRepository userAccountRepository, VerificationTokenRepository verificationTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
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

        userAccount.setPassword(this.passwordEncoder.encode(userAccount.getPassword()));

        String token = generateToken();
        VerificationToken verificationToken = new VerificationToken(token, userAccount);

        this.emailService.sendVerificationEmail(userAccount, verificationToken.getToken());
        this.verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void verifyNewUserAccount(String token) throws InvalidTokenException {
        VerificationToken verificationToken = this.verificationTokenRepository.findByToken(token);

        if (verificationToken == null) {
            throw new InvalidTokenException();
        }

        UserAccount userAccount = verificationToken.getUserAccount();

        if (userAccount == null) {
            throw new InvalidTokenException();
        }

        this.userAccountRepository.save(userAccount);
        this.verificationTokenRepository.delete(verificationToken);
    }

}
