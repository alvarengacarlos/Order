package com.alvarengacarlos.order.www;

public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerPreRegisterRepository customerPreRegisterRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerPreRegisterRepository customerPreRegisterRepository) {
        this.customerRepository = customerRepository;
        this.customerPreRegisterRepository = customerPreRegisterRepository;
    }

    public void preRegisterCustomer(String phoneNumber) {
        CustomerPreRegister customerPreRegister = CustomerPreRegister.newCustomerPreRegister(phoneNumber);
        //TODO: these two operations can be parallel
        customerPreRegisterRepository.saveCustomerPreRegister(customerPreRegister);
        customerPreRegisterRepository.sendSmsToCustomer(phoneNumber, "...");
    }

    public void registerCustomer(
            String phoneNumber,
            String validationCode,
            String name
    ) throws InvalidValidationCodeException {
        CustomerPreRegister customerPreRegister = customerPreRegisterRepository.findCustomerPreRegister(
                phoneNumber
        );
        if (customerPreRegister == null || !customerPreRegister.validationCode.equals(validationCode)) {
            throw new InvalidValidationCodeException();
        }

        Customer customer = new Customer(name, phoneNumber);
        customerRepository.saveCustomer(customer);
    }
}
