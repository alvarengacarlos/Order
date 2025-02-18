package com.alvarengacarlos.order.www;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeRepository employeeRepository;
    private CreateEmployeeDto createEmployeeDto = new CreateEmployeeDto(
            "John Doe",
            "john",
            "John@123",
            Role.EMPLOYEE_COOK
    );

    @BeforeEach
    void beforeEach() {
        employeeRepository = Mockito.mock();
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    void shouldThrowEmployeeExists() {
        Employee employee = Mockito.mock(Employee.class);
        Mockito.when(employeeRepository.findEmployeeByUsername(createEmployeeDto.username())).thenReturn(employee);

        Assertions.assertThrows(EmployeeExistsException.class, () -> employeeService.createEmployee(createEmployeeDto));
        Mockito.verify(employeeRepository, Mockito.times(1)).findEmployeeByUsername(createEmployeeDto.username());
    }

    @Test
    void shouldSaveAEmployee() {
        Mockito.when(employeeRepository.findEmployeeByUsername(createEmployeeDto.username())).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> employeeService.createEmployee(createEmployeeDto));
        Mockito.verify(employeeRepository, Mockito.times(1)).findEmployeeByUsername(createEmployeeDto.username());
        Mockito.verify(employeeRepository, Mockito.times(1)).saveEmployee(Mockito.any(SaveEmployeeDto.class));
    }

    @Test
    void shouldDestroyAEmployee() {
        Employee employee = Mockito.mock();

        Assertions.assertDoesNotThrow(() -> employeeService.destroyEmployee(employee.id()));
        Mockito.verify(employeeRepository).deleteEmployee(employee.id());

    }
}
