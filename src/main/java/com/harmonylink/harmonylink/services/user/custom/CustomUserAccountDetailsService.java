package com.harmonylink.harmonylink.services.user.custom;

import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserAccountDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;


    @Autowired
    public CustomUserAccountDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserAccount userAccount = this.userAccountRepository.findByLogin(login.toLowerCase());

        if (userAccount == null) {
            userAccount = this.userAccountRepository.findByEmail(login.toLowerCase());
        }

        if (userAccount == null) {
            throw new UsernameNotFoundException("Użytkownik o loginie '" + login + "' nie istnieje");
        }

        return userAccount;
    }

}
