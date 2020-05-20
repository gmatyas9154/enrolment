package com.mgl.enrolment.faults.exceptions;

public class EnrolmentException extends RuntimeException {

    public EnrolmentException(String message) {
        super(message);
    }

    public EnrolmentException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public EnrolmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
