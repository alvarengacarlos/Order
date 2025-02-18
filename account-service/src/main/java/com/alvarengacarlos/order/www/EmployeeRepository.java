package com.alvarengacarlos.order.www;

public interface EmployeeRepository {

    void saveEmployee(SaveEmployeeDto saveEmployeeDto);

    Employee findEmployeeByUsername(String username);
}
