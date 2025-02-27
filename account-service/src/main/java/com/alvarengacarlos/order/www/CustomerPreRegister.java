package com.alvarengacarlos.order.www;

import java.security.SecureRandom;
import java.util.HexFormat;

public class CustomerPreRegister {

    public final String phoneNumber;
    public final String validationCode;

    public CustomerPreRegister(String phoneNumber, String validationCode) {
        this.phoneNumber = phoneNumber;
        this.validationCode = validationCode;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("CustomerPreRegister{");
        str.append("phoneNumber=").append(phoneNumber);
        str.append(", validationCode=").append(validationCode);
        str.append('}');
        return str.toString();
    }

    public static CustomerPreRegister newCustomerPreRegister(String phoneNumber) {
        return new CustomerPreRegister(phoneNumber, generateValidationCode());
    }

    public static String generateValidationCode() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] buffer = new byte[3];
        secureRandom.nextBytes(buffer);
        return HexFormat.of().formatHex(buffer);
    }
}
