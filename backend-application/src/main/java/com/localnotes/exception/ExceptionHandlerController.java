package com.localnotes.exception;

import com.localnotes.security.jwt.JwtAuthenticationException;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> wrongInputData(IllegalArgumentException exception) {
        UUID exceptionId = UUID.randomUUID();
        log.error("Wrong data used. Message: {}. [exceptionId = {}]",
                exception.getMessage(), exceptionId, exception);
        return new ResponseEntity<>(
                "Illegal argument was used, contact support. ID: " + exceptionId,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> notFoundData(EntityNotFoundException exception) {
        UUID exceptionId = UUID.randomUUID();
        log.error(
                "Data was not found. Message: {}. [exceptionId = {}]",
                exception.getMessage(), exceptionId, exception);
        return new ResponseEntity<>(
                "Data was not found. If this is a mistake, contact support. ID: " + exceptionId,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<String> wrongAuthData(JwtAuthenticationException exception) {
        UUID exceptionId = UUID.randomUUID();
        log.error(
                "Wrong authentication data. Message: {}. [exceptionId = {}]",
                exception.getMessage(), exceptionId, exception);
        return new ResponseEntity<>(
                "Wrong authentication data. Incorrect username/password. "
                        + "If this is a mistake, contact support. ID: " + exceptionId,
                HttpStatus.BAD_REQUEST);
    }
}
