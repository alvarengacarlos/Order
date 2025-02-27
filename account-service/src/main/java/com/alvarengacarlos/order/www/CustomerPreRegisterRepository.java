package com.alvarengacarlos.order.www;

public interface CustomerPreRegisterRepository {

    void saveCustomerPreRegister(CustomerPreRegister customerPreRegister);

    void sendSmsToCustomer(String phoneNumber, String text);

    CustomerPreRegister findCustomerPreRegister(String phoneNumber);
}
