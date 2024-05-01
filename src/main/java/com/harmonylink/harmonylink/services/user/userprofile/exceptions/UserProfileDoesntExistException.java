package com.harmonylink.harmonylink.services.user.userprofile.exceptions;

public class UserProfileDoesntExistException extends Exception {

    public UserProfileDoesntExistException(String userProfileId) {
        super("Użytkownik o id profilu: " + userProfileId + " nie istnieje");
    }

}
