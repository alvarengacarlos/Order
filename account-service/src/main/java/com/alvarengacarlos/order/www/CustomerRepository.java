package com.alvarengacarlos.order.www;

public interface CustomerRepository {

    void saveCustomerPreRegister(String phoneNumber, String validationCode);

    void sendSmsToCustomer(String phoneNumber, String text);

    String findCustomerPreRegister(String phoneNumber);

    void saveCustomerRegister(String name, String phoneNumber);
}
