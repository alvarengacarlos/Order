package com.alvarengacarlos.order.www;

public record SaveEmployeeDto(
    String name,
    String username,
    String passwordHash,
    String salt,
    EmployeeRole employeeRole
) {}
