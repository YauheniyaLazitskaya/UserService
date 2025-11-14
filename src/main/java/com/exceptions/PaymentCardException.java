package com.exceptions;

public class PaymentCardException extends RuntimeException {
    public PaymentCardException(String message) {
        super(message);
    }
    public PaymentCardException(String message, Throwable cause) { super(message, cause); }
}
