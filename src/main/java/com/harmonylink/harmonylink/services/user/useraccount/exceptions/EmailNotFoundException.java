package com.harmonylink.harmonylink.services.user.useraccount.exceptions;

public class EmailNotFoundException extends Exception {

    public EmailNotFoundException(String email) {
        super("Nie możemy wysłać wiadmości z potwierdzeniem na maila '" + email + "'");
    }

}
