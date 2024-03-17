package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class PasswordsMatchingException extends Exception {

    public PasswordsMatchingException() {
        super("Hasła nie pasują");
    }

}
