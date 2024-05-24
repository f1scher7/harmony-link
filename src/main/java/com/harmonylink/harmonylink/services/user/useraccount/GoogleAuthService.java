package com.harmonylink.harmonylink.services.user.useraccount;

import com.harmonylink.harmonylink.enums.Role;
import com.harmonylink.harmonylink.models.user.UserAccount;
import com.harmonylink.harmonylink.repositories.user.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.harmonylink.harmonylink.utils.UserUtil.uniqueLoginGenerator;

@Service
public class GoogleAuthService {

    private final UserAccountRepository userAccountRepository;


    @Autowired
    public GoogleAuthService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Async
    public void saveOrUpdateUserAccount(UserAccount userAccount, String ip) {
        UserAccount userGoogleAccount = this.userAccountRepository.findByGoogleId(userAccount.getGoogleId());

        if (userGoogleAccount == null) {
            userGoogleAccount = new UserAccount();
            userGoogleAccount.setGoogleId(userAccount.getGoogleId());
            userGoogleAccount.addIpAddress(ip);
        }

        List<String> ips = userGoogleAccount.getIpAddresses();

        if (!ips.contains(ip)) {
            userGoogleAccount.addIpAddress(ip);
        }

        userGoogleAccount.setRole(Role.USER);
        userGoogleAccount.setLogin(uniqueLoginGenerator(userAccount.getEmail()));
        userGoogleAccount.setDisplayName(userAccount.getDisplayName());
        userGoogleAccount.setEmail(userAccount.getEmail());
        userGoogleAccount.setProfilePictureUrl(userAccount.getProfilePictureUrl());

        this.userAccountRepository.save(userGoogleAccount);
    }

}

