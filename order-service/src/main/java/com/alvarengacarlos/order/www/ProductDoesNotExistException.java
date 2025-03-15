package com.alvarengacarlos.order.www;

public class ProductDoesNotExistException extends Exception {

    public ProductDoesNotExistException() {
        super("Product does not exist");
    }
}
