package com.alvarengacarlos.order.www;

public class AuthenticationFailureException extends Exception {

    public AuthenticationFailureException() {
        super(
            "The username or password are incorrect or employee may be inactive"
        );
    }
}
