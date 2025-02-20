package com.alvarengacarlos.order.www;

import java.util.UUID;

public record Employee(
    UUID id,
    String name,
    String username,
    String passwordHash,
    String salt,
    EmployeeRole employeeRole,
    Boolean isActive
) {}
