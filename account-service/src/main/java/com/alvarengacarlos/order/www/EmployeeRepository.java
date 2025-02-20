package com.alvarengacarlos.order.www;

import java.util.UUID;

public interface EmployeeRepository {
    void saveEmployee(SaveEmployeeDto saveEmployeeDto);

    Employee findEmployeeByUsername(String username);

    void deleteEmployee(UUID employeeId);

    void updateIsActiveEmployeeAttribute(UUID employeeId, Boolean active);
}
