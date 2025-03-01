package com.alvarengacarlos.order.www;

public interface CustomerPreRegistrationRepository {

    void saveCustomerPreRegistration(CustomerPreRegistration customerPreRegister);

    void sendSmsToCustomer(String phoneNumber, String text);

    CustomerPreRegistration findCustomerPreRegistration(String phoneNumber);
}
