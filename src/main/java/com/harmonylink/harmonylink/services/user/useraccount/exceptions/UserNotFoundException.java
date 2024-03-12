package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("User not found.");
    }

}
