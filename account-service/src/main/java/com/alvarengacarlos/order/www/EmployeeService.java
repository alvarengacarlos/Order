package com.alvarengacarlos.order.www;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final String jwtSecret;

    public EmployeeService(
        EmployeeRepository employeeRepository,
        String jwtSecret
    ) {
        this.employeeRepository = employeeRepository;
        this.jwtSecret = jwtSecret;
    }

    public void createEmployee(CreateEmployeeDto createEmployeeDto)
        throws EmployeeExistsException {
        Employee employee = employeeRepository.findEmployeeByUsername(
            createEmployeeDto.username()
        );
        if (employee != null) {
            throw new EmployeeExistsException();
        }

        String salt = generateSalt();
        SaveEmployeeDto saveEmployeeDto = new SaveEmployeeDto(
            createEmployeeDto.name(),
            createEmployeeDto.username(),
            hashEmployeePassword(createEmployeeDto.password(), salt),
            salt,
            createEmployeeDto.employeeRole()
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

    public void activateEmployee(UUID employeeId) {
        employeeRepository.updateIsActiveEmployeeAttribute(employeeId, true);
    }

    public void deactivateEmployee(UUID employeeId) {
        employeeRepository.updateIsActiveEmployeeAttribute(employeeId, false);
    }

    public String authenticateEmployee(String username, String password)
        throws AuthenticationFailureException {
        Employee employee = employeeRepository.findEmployeeByUsername(username);

        if (employee == null) {
            throw new AuthenticationFailureException();
        }

        if (!employee.isActive()) {
            throw new AuthenticationFailureException();
        }

        if (
            !employee
                .passwordHash()
                .equals(hashEmployeePassword(password, employee.salt()))
        ) {
            throw new AuthenticationFailureException();
        }

        return generateJwtToken(employee);
    }

    private String generateJwtToken(Employee employee) {
        Map<String, String> payload = Map.of(
            "id",
            employee.id().toString(),
            "role",
            employee.employeeRole().toString()
        );
        Instant expiresAt = Instant.now().plusSeconds(60 * 60 * 24);
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.create()
            .withPayload(payload)
            .withExpiresAt(expiresAt)
            .sign(algorithm);
    }
}
