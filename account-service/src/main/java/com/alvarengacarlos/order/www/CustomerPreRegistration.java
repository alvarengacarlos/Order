package com.alvarengacarlos.order.www;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

public class CustomerPreRegistration {

    public final String phoneNumber;
    public final String validationCode;
    public final Instant expiresAt;

    public CustomerPreRegistration(String phoneNumber, String validationCode, Instant expiresAt) {
        this.phoneNumber = phoneNumber;
        this.validationCode = validationCode;
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CustomerPreRegistration{");
        sb.append("phoneNumber=").append(phoneNumber);
        sb.append(", validationCode=").append(validationCode);
        sb.append(", expiresAt=").append(expiresAt.minusNanos(expiresAt.getNano()));
        sb.append('}');
        return sb.toString();
    }

    public static CustomerPreRegistration newCustomerPreRegister(String phoneNumber) {
        Instant expiresAt = Instant.now().plusSeconds(24 * 60 * 60);
        return new CustomerPreRegistration(phoneNumber, generateValidationCode(), expiresAt);
    }

    public static String generateValidationCode() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] buffer = new byte[3];
        secureRandom.nextBytes(buffer);
        return HexFormat.of().formatHex(buffer);
    }

}
