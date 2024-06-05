package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.token.ResetPasswordToken;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.token.ResetPasswordTokenRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.EmailNotFoundException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.InvalidPasswordException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.InvalidTokenException;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.PasswordsMatchingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.harmonylink.harmonylink.utils.UserUtil.generateToken;
import static com.harmonylink.harmonylink.utils.UserUtil.isPasswordValid;

@Service
public class ResetPasswordService {

    private final UserAccountRepository userAccountRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public ResetPasswordService(UserAccountRepository userAccountRepository, ResetPasswordTokenRepository resetPasswordTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public void forgotPassword(String email) throws EmailNotFoundException {
        UserAccount userAccount = this.userAccountRepository.findByEmail(email);

        if (userAccount == null) {
            throw new EmailNotFoundException(email);
        }

        String token = generateToken();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, userAccount);

        this.emailService.sendResetPasswordEmail(userAccount, resetPasswordToken.getToken());
        this.resetPasswordTokenRepository.save(resetPasswordToken);
    }

    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) throws InvalidTokenException, InvalidPasswordException, PasswordsMatchingException {
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

        userAccount.setPassword(passwordEncoder.encode(newPassword));
        this.userAccountRepository.save(userAccount);
        this.resetPasswordTokenRepository.delete(resetPasswordToken);
    }

}
