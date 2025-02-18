package com.alvarengacarlos.order.www;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void createEmployee(CreateEmployeeDto createEmployeeDto) throws EmployeeExistsException {
        Employee employee = employeeRepository.findEmployeeByUsername(createEmployeeDto.username());
        if (employee != null) {
            throw new EmployeeExistsException();
        }

        String salt = generateSalt();
        SaveEmployeeDto saveEmployeeDto = new SaveEmployeeDto(
                createEmployeeDto.name(),
                createEmployeeDto.username(),
                hashEmployeePassword(createEmployeeDto.password(), salt),
                salt,
                createEmployeeDto.role()
        );
        employeeRepository.saveEmployee(saveEmployeeDto);
    }

    private String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] buffer = new byte[16];
        secureRandom.nextBytes(buffer);
        return HexFormat.of().formatHex(buffer);
    }

    private String hashEmployeePassword(String employeePassword, String salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt.getBytes());
            messageDigest.update(employeePassword.getBytes());
            byte[] buffer = new byte[32];
            messageDigest.digest(buffer, 0, buffer.length);
            return HexFormat.of().formatHex(buffer);
        } catch (NoSuchAlgorithmException | DigestException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public void destroyEmployee(UUID employeeId) {
        employeeRepository.deleteEmployee(employeeId);
    }
}
