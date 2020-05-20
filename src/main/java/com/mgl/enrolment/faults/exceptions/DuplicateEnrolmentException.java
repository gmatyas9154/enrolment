package com.mgl.enrolment.faults.exceptions;

import lombok.Getter;

public class DuplicateEnrolmentException extends EnrolmentException{

    @Getter
    private String field;

    public DuplicateEnrolmentException(String message, String field) {
        super(message);
        this.field = field;
    }
}
