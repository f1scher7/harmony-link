package com.harmonylink.harmonylink.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;


public class UserAccountUtil {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[a-z].*") && password.matches(".*[0-9].*");
    }

    public static boolean isUserAtLeastSixteenYO(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthdate, currentDate);
        int age = period.getYears();

        return age >= 16;
    }

}
