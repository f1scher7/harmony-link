package com.harmonylink.harmonylink.services.user.userprofile.exceptions;

public class InvalidUserFieldOfStudyException extends Exception {

    public InvalidUserFieldOfStudyException() {
        super("Prosimy podać aktualny lub ostatni kierunek studiów/edukacji");
    }

}
