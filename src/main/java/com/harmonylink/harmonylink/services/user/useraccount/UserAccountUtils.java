package com.harmonylink.harmonylink.services.user.useraccount;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


public class UserAccountUtils {

    public static String updateUserIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

}
