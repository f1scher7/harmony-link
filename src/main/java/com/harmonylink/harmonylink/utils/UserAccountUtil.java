package com.harmonylink.harmonylink.utils;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import static java.lang.Character.isLetterOrDigit;

public class UserAccountUtil {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean isLoginValid(String login) {
        if (login == null || login.length() < 4 || login.length() > 12) {
            return false;
        }

        for (int i = 0; i < login.length(); i++) {
            char c = login.charAt(i);
            if (!isLetterOrDigit(c)) {
                return false;
            }
        }

        return true;
    }

    public static String uniqueLoginGenerator(String email) {
        return email.replaceAll("@.*", "") + generateToken();
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
