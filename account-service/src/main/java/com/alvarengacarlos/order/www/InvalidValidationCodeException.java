package com.alvarengacarlos.order.www;

public class InvalidValidationCodeException extends Exception {
    public InvalidValidationCodeException() {
        super("Invalid validation code");
    }
}
