package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class FailedGoogleLoginException extends RuntimeException {

    public FailedGoogleLoginException(String email) {
        super("Mail " + email + " jest już zajęty spróbuj zalogować się przez zwykłe logowanie");
    }

}