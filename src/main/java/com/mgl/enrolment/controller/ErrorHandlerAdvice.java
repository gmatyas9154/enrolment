package com.mgl.enrolment.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.mgl.enrolment.dto.validation.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandlerAdvice {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse.ErrorResponseBuilder errorsBuilder = ErrorResponse.builder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            ErrorResponse.Fault fault = ErrorResponse.Fault.builder()
                    .fieldName(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
            errorsBuilder.fault(fault);
        }
        return errorsBuilder.build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse onMessageNotReadable(HttpMessageNotReadableException e) {
        ErrorResponse.ErrorResponseBuilder errorsBuilder = ErrorResponse.builder();
        ErrorResponse.Fault fault = null;
        Throwable cause = e.getCause();
        if (cause != null) {
            if (cause instanceof InvalidFormatException) {
                InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
                String path = invalidFormatException.getPath().stream()
                        .map(JsonMappingException.Reference::getFieldName)
                        .collect(Collectors.joining("."));
                String value = String.valueOf(invalidFormatException.getValue());
                fault = ErrorResponse.Fault.builder()
                        .fieldName(path)
                        .message("Invalid format for value: " + value)
                        .build();
            } else if (cause instanceof JsonMappingException) {
                fault = ErrorResponse.Fault.builder()
                        .fieldName(".")
                        .message("Malformed JSON")
                        .build();
            }
        }

        if (fault == null) {
            fault = ErrorResponse.Fault.builder()
                    .fieldName(".")
                    .message("Unable to process Request")
                    .build();
        }
        return errorsBuilder.fault(fault).build();
    }
}
