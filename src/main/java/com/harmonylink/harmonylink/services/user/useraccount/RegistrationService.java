package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.enums.Role;
import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.models.token.VerificationToken;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.repositories.token.VerificationTokenRepository;
import com.harmonylink.harmonylink.services.user.custom.CustomUserAccountDetailsService;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.List;

import static com.harmonylink.harmonylink.utils.UserAccountUtil.*;

@Service
public class RegistrationService {

    private final CustomUserAccountDetailsService customUserAccountDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public RegistrationService(CustomUserAccountDetailsService customUserAccountDetailsService, AuthenticationManager authenticationManager, UserAccountRepository userAccountRepository, VerificationTokenRepository verificationTokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.customUserAccountDetailsService = customUserAccountDetailsService;
        this.authenticationManager = authenticationManager;
        this.userAccountRepository = userAccountRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public void registerNewUserAccount(UserAccount userAccount) throws UserAlreadyExistsException, InvalidPasswordException, EmailAlreadyExistsException, EmailNotFoundException, UserTooYoungException, InvalidLoginException {
        if (userAccount == null) {
            throw new IllegalArgumentException("UserAccount can't be null");
        }

        if (this.userAccountRepository.findByLogin(userAccount.getLogin()) != null) {
            throw new UserAlreadyExistsException(userAccount.getLogin());
        }

        if (!isLoginValid(userAccount.getLogin())) {
            throw new InvalidLoginException();
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
        userAccount.setRole(Role.TEMP_USER);

        String token = generateToken();
        VerificationToken verificationToken = new VerificationToken(token, userAccount);

        this.emailService.sendVerificationEmail(userAccount, verificationToken.getToken());
        this.userAccountRepository.save(userAccount);
        this.verificationTokenRepository.save(verificationToken);
    }

    public void autoLogin(String login, String password) {
        UserAccount userAccount = (UserAccount) this.customUserAccountDetailsService.loadUserByUsername(login);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userAccount, password, userAccount.getAuthorities());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpServletRequest newRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession newSession = newRequest.getSession(true);
            newSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        }
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

        userAccount.setRole(Role.USER);
        this.userAccountRepository.save(userAccount);
        this.verificationTokenRepository.delete(verificationToken);
    }

    @Scheduled(fixedRate = 10000)
    public void deleteUnconfirmedUserAccounts() {
        List<UserAccount> userAccounts = this.userAccountRepository.findAllByRole(Role.TEMP_USER);

        for (UserAccount userAccount: userAccounts) {
            VerificationToken token = this.verificationTokenRepository.findByUserAccount(userAccount);

            if (token == null) {
                this.userAccountRepository.delete(userAccount);
            }
        }
    }

}
