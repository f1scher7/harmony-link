package com.harmonylink.harmonylink.utils;

import com.harmonylink.harmonylink.models.user.userprofile.UserProfile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import static java.lang.Character.isLetter;
import static java.lang.Character.isLetterOrDigit;

public final class UserUtil {

    public static Pageable createPageableWithLimit(int limit) {
        return PageRequest.of(0, limit);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean isStringContainsOnlyLetters(String string) {
        String stringWithoutSpaces = string.replaceAll(" ", "");

        for (int i = 0; i < stringWithoutSpaces.length(); i++) {
            char c = stringWithoutSpaces.charAt(i);
            if (!isLetter(c)) {
                return false;
            }
        }

        return true;
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

    public static int getUserAge(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthdate, currentDate);

        return period.getYears();
    }

    public static String getUserProfilesId(UserProfile userProfile1, UserProfile userProfile2) {
        return userProfile1.getId() + userProfile2.getId();
    }

}
