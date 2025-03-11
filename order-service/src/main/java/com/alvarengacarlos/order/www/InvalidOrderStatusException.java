package com.alvarengacarlos.order.www;

public class InvalidOrderStatusException extends Exception {

    public InvalidOrderStatusException() {
        super("Invalid order status");
    }
}
