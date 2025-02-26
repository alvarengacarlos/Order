package com.alvarengacarlos.order.www;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeRepository employeeRepository;
    private final UUID employeeId = UUID.randomUUID();
    private final String employeeName = "John Doe";
    private final String employeeUsername = "JohnDoe";
    private final String employeePassword = "John@123";
    private final EmployeeRole employeeRole = EmployeeRole.COOK;
    private final CreateEmployeeDto createEmployeeDto = new CreateEmployeeDto(
            employeeName,
            employeeUsername,
            employeePassword,
            employeeRole
    );
    private final Employee employee = new Employee(
            employeeId,
            employeeName,
            employeeUsername,
            "fbe8c52af91b1a366df1982568e83d36bc3a546abcb7ebc55033750bd5a06a9d",
            "1234",
            employeeRole,
            true
    );

    @BeforeEach
    void beforeEach() {
        employeeRepository = Mockito.mock();
        String jwtSecret = "jwt-secret";
        employeeService = new EmployeeService(employeeRepository, jwtSecret);
    }

    @Nested
    class CreateEmployee {

        @Test
        void shouldThrowEmployeeExists() {
            Employee employee = Mockito.mock(Employee.class);
            Mockito.when(
                    employeeRepository.findEmployeeByUsername(
                            createEmployeeDto.username()
                    )
            ).thenReturn(employee);

            Assertions.assertThrows(EmployeeExistsException.class, ()
                    -> employeeService.createEmployee(createEmployeeDto)
            );
            Mockito.verify(
                    employeeRepository,
                    Mockito.times(1)
            ).findEmployeeByUsername(createEmployeeDto.username());
        }

        @Test
        void shouldSaveAEmployee() {
            Mockito.when(
                    employeeRepository.findEmployeeByUsername(
                            createEmployeeDto.username()
                    )
            ).thenReturn(null);

            Assertions.assertDoesNotThrow(()
                    -> employeeService.createEmployee(createEmployeeDto)
            );
            Mockito.verify(
                    employeeRepository,
                    Mockito.times(1)
            ).findEmployeeByUsername(createEmployeeDto.username());
            Mockito.verify(employeeRepository, Mockito.times(1)).saveEmployee(
                    Mockito.any(Employee.class)
            );
        }
    }

    @Nested
    class DestroyEmployee {

        @Test
        void shouldDestroyAEmployee() {
            Assertions.assertDoesNotThrow(()
                    -> employeeService.destroyEmployee(employeeId)
            );
            Mockito.verify(employeeRepository, Mockito.times(1)).deleteEmployee(
                    employeeId
            );
        }
    }

    @Nested
    class ActivateEmployee {

        @Test
        void shouldActivateAEmployee() {
            Assertions.assertDoesNotThrow(()
                    -> employeeService.activateEmployee(employeeId)
            );
            Mockito.verify(
                    employeeRepository,
                    Mockito.times(1)
            ).updateIsActiveEmployeeAttribute(employeeId, true);
        }
    }

    @Nested
    class DeactivateEmployee {

        @Test
        void shouldDeactivateAEmployee() {
            Assertions.assertDoesNotThrow(()
                    -> employeeService.deactivateEmployee(employeeId)
            );
            Mockito.verify(
                    employeeRepository,
                    Mockito.times(1)
            ).updateIsActiveEmployeeAttribute(employeeId, false);
        }
    }

    @Nested
    class AuthenticateEmployee {

        @Nested
        class ShouldThrowAuthenticationFailureException {

            @Test
            void whenUsernameIsInvalid() {
                Mockito.when(
                        employeeRepository.findEmployeeByUsername(employeeUsername)
                ).thenReturn(null);

                Assertions.assertThrows(
                        AuthenticationFailureException.class,
                        ()
                        -> employeeService.authenticateEmployee(
                                employeeUsername,
                                employeePassword
                        )
                );
                Mockito.verify(
                        employeeRepository,
                        Mockito.times(1)
                ).findEmployeeByUsername(employeeUsername);
            }

            @Test
            void whenEmployeeIsInactive() {
                Employee employee = new Employee(
                        employeeId,
                        employeeName,
                        employeeUsername,
                        "",
                        "",
                        employeeRole,
                        false
                );
                Mockito.when(
                        employeeRepository.findEmployeeByUsername(employeeUsername)
                ).thenReturn(employee);

                Assertions.assertThrows(
                        AuthenticationFailureException.class,
                        ()
                        -> employeeService.authenticateEmployee(
                                employeeUsername,
                                employeePassword
                        )
                );
                Mockito.verify(
                        employeeRepository,
                        Mockito.times(1)
                ).findEmployeeByUsername(employeeUsername);
            }

            @Test
            void whenPasswordIsInvalid() {
                Mockito.when(
                        employeeRepository.findEmployeeByUsername(employeeUsername)
                ).thenReturn(null);

                Assertions.assertThrows(
                        AuthenticationFailureException.class,
                        ()
                        -> employeeService.authenticateEmployee(
                                employeeUsername,
                                employeePassword
                        )
                );
                Mockito.verify(
                        employeeRepository,
                        Mockito.times(1)
                ).findEmployeeByUsername(employeeUsername);
            }
        }

        @Test
        void shouldAuthenticateAEmployee()
                throws AuthenticationFailureException {
            Mockito.when(
                    employeeRepository.findEmployeeByUsername(employeeUsername)
            ).thenReturn(employee);

            String jwtToken = employeeService.authenticateEmployee(
                    employeeUsername,
                    employeePassword
            );

            Mockito.verify(
                    employeeRepository,
                    Mockito.times(1)
            ).findEmployeeByUsername(employeeUsername);
            Assertions.assertNotNull(jwtToken);
        }
    }
}
