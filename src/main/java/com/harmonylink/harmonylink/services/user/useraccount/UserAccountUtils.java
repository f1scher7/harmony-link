package com.harmonylink.harmonylink.services.user.useraccount;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;


public class UserAccountUtils {

    public static String updateUserIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static String generatePasswordResetToken() {
        return UUID.randomUUID().toString();
    }

}
