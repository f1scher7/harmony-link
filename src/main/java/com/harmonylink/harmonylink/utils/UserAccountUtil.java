package com.harmonylink.harmonylink.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;


public class UserAccountUtils {

    public static String updateUserIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[0-9].*");
    }

    public static SimpleMailMessage generateEmail(String userMail, String subject, String templateName, Context context) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        TemplateEngine templateEngine = new TemplateEngine();

        String body = templateEngine.process(templateName, context);
        String harmonyEmail = System.getenv("HARMONYLINK_EMAIL");

        simpleMailMessage.setFrom(harmonyEmail);
        simpleMailMessage.setTo(userMail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        return simpleMailMessage;
    }

    public static boolean isUserAtLeastSixteenYO(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthdate, currentDate);
        int age = period.getYears();

        return age >= 16;
    }

}
