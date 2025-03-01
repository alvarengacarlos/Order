package com.alvarengacarlos.order.www;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

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

        Employee newEmployee = Employee.newEmployee(
                createEmployeeDto.name(),
                createEmployeeDto.username(),
                createEmployeeDto.password(),
                createEmployeeDto.employeeRole()
        );
        employeeRepository.saveEmployee(newEmployee);
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

    public String authenticateEmployee(AuthenticateEmployeeDto authenticateEmployeeDto)
            throws AuthenticationFailureException {
        Employee employee = employeeRepository.findEmployeeByUsername(authenticateEmployeeDto.username);

        if (employee == null) {
            throw new AuthenticationFailureException();
        }

        if (!employee.isActive) {
            throw new AuthenticationFailureException();
        }

        if (!employee.passwordHash.equals(Employee.hashPassword(authenticateEmployeeDto.password, employee.salt))) {
            throw new AuthenticationFailureException();
        }

        return generateJwtToken(employee);
    }

    private String generateJwtToken(Employee employee) {
        Map<String, String> payload = Map.of(
                "id",
                employee.id.toString(),
                "role",
                employee.employeeRole.toString()
        );
        Instant expiresAt = Instant.now().plusSeconds(60 * 60 * 24);
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.create()
                .withPayload(payload)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }
}
