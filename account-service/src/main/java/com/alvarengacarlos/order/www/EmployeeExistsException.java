package com.alvarengacarlos.order.www;

public class EmployeeExistsException extends Exception {

    public EmployeeExistsException() {
        super("The employee exists");
    }
}
