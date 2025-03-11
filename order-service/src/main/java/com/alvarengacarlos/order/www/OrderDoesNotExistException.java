package com.alvarengacarlos.order.www;

public class OrderDoesNotExistException extends Exception {

    public OrderDoesNotExistException() {
        super("Order does not exist");
    }
}
