package com.exceptions;

import com.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionDTO> handleUserException(UserException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND; // 404
        } else if (ex.getMessage().contains("already")) {
            status = HttpStatus.CONFLICT; // 409
        }
        ExceptionDTO userEx = new ExceptionDTO(status.value(), ex.getMessage());
        return new ResponseEntity<>(userEx, status);
    }

    @ExceptionHandler(PaymentCardException.class)
    public ResponseEntity<ExceptionDTO> handlePaymentCardException(PaymentCardException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getMessage().contains("not found") || ex.getMessage().contains("doesn't have")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("already")) {
            status = HttpStatus.CONFLICT;
        }
        ExceptionDTO cardEx = new ExceptionDTO(status.value(), ex.getMessage());
        return new ResponseEntity<>(cardEx, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(new ExceptionDTO(status.value(), errorMessage), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ExceptionDTO(status.value(),
                "An internal server error occurred."), status);
    }
}
