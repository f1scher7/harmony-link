package com.harmonylink.harmonylink.services.user.useraccount;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;


public class UserAccountUtils {

    public static String updateUserIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static SimpleMailMessage mailGenerator(String userMail, String subject, String text) {
        String harmonyEmail = System.getenv("HARMONYLINK_EMAIL");

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(harmonyEmail);
        simpleMailMessage.setTo(userMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);

        return simpleMailMessage;
    }

}
