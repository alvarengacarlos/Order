package com.alvarengacarlos.order.www;

public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerPreRegistrationRepository customerPreRegisterRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerPreRegistrationRepository customerPreRegisterRepository) {
        this.customerRepository = customerRepository;
        this.customerPreRegisterRepository = customerPreRegisterRepository;
    }

    public void preRegisterCustomer(PreRegisterCustomerDto preRegisterCustomerDto) {
        CustomerPreRegistration customerPreRegister = CustomerPreRegistration.newCustomerPreRegister(preRegisterCustomerDto.phoneNumber());
        //TODO: these two operations can be parallel
        customerPreRegisterRepository.saveCustomerPreRegistration(customerPreRegister);
        customerPreRegisterRepository.sendSmsToCustomer(preRegisterCustomerDto.phoneNumber(), "...");
    }

    public void registerCustomer(RegisterCustomerDto registerCustomerDto) throws InvalidValidationCodeException {
        CustomerPreRegistration customerPreRegister = customerPreRegisterRepository.findCustomerPreRegistration(
                registerCustomerDto.phoneNumber()
        );
        if (customerPreRegister == null || !customerPreRegister.validationCode.equals(registerCustomerDto.validationCode())) {
            throw new InvalidValidationCodeException();
        }

        Customer customer = new Customer(registerCustomerDto.name(), registerCustomerDto.phoneNumber());
        customerRepository.saveCustomer(customer);
    }
}
