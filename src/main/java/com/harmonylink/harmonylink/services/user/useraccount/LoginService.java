package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import com.harmonylink.harmonylink.services.user.useraccount.exceptions.InvalidCredentialsException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final Logger USER_LOGIN_LOGGER = LoggerFactory.getLogger("UserLogin");

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public LoginService(UserAccountRepository userAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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

}
