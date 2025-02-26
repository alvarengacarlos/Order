package com.alvarengacarlos.order.www;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

public class Employee {

    public final UUID id;
    public final String name;
    public final String username;
    public final String passwordHash;
    public final String salt;
    public final EmployeeRole employeeRole;
    public final Boolean isActive;

    public Employee(
            UUID id,
            String name,
            String username,
            String passwordHash,
            String salt,
            EmployeeRole employeeRole,
            Boolean isActive
    ) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.employeeRole = employeeRole;
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Employee{");
        str.append("id=").append(id);
        str.append(", name=").append(name);
        str.append(", username=").append(username);
        str.append(", passwordHash=").append(passwordHash);
        str.append(", salt=").append(salt);
        str.append(", employeeRole=").append(employeeRole);
        str.append(", isActive=").append(isActive);
        str.append('}');
        return str.toString();
    }

    public static Employee newEmployee(
            String name,
            String username,
            String password,
            EmployeeRole employeeRole
    ) {
        String salt = generateSalt();
        return new Employee(
                UUID.randomUUID(),
                name,
                username,
                hashPassword(password, salt),
                salt,
                employeeRole,
                true
        );
    }

    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] buffer = new byte[16];
        secureRandom.nextBytes(buffer);
        return HexFormat.of().formatHex(buffer);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt.getBytes());
            messageDigest.update(password.getBytes());
            byte[] buffer = new byte[32];
            messageDigest.digest(buffer, 0, buffer.length);
            return HexFormat.of().formatHex(buffer);
        } catch (NoSuchAlgorithmException | DigestException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
