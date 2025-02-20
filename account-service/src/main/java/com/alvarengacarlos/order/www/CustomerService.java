package com.alvarengacarlos.order.www;

import java.security.SecureRandom;
import java.util.HexFormat;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void preRegisterCustomer(String phoneNumber) {
        String validationCode = generateValidationCode();
        //TODO: these two operations can be parallel
        customerRepository.saveCustomerPreRegister(phoneNumber, validationCode);
        customerRepository.sendSmsToCustomer(phoneNumber, "...");
    }

    private String generateValidationCode() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] buffer = new byte[3];
        secureRandom.nextBytes(buffer);
        return HexFormat.of().formatHex(buffer);
    }

    public void registerCustomer(String phoneNumber, String validationCode, String name) throws InvalidValidationCodeException {
        String preRegister = customerRepository.findCustomerPreRegister(phoneNumber);
        if (preRegister == null || !preRegister.equals(validationCode)) {
            throw new InvalidValidationCodeException();
        }

        customerRepository.saveCustomerRegister(name, phoneNumber);
    }
}
