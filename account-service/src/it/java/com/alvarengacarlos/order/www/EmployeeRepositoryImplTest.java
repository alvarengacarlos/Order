package com.alvarengacarlos.order.www;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class EmployeeRepositoryImplTest {

    private static DynamoDbClient dynamoDbClient;
    private static EmployeeRepositoryImpl employeeRepositoryImpl;
    private static DynamoDbHelper dynamoDbHelper;
    private final Faker faker = new Faker();
    private final String name = faker.name().name();
    private final String username = faker.name().username();
    private final String password = faker.internet().password();
    private final EmployeeRole role = EmployeeRole.COOK;

    @BeforeAll
    static void beforeAll() {
        Integer port = DynamoDbContainerSingleton.getInstance().getPort();
        dynamoDbHelper = new DynamoDbHelper(port);
        dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        employeeRepositoryImpl = new EmployeeRepositoryImpl(dynamoDbClient);
    }

    @Nested
    class SaveEmployee {

        @Test
        void shouldSaveAEmployee() {
            Employee newEmployee = Employee.newEmployee(
                    name,
                    username,
                    password,
                    role
            );
            Assertions.assertDoesNotThrow(() -> employeeRepositoryImpl.saveEmployee(newEmployee));
            Assertions.assertEquals(employeeRepositoryImpl.findEmployeeByUsername(newEmployee.username).toString(), newEmployee.toString());
        }

    }

    @Nested
    class FindEmployeeByUsername {

        @Test
        void shouldFindAEmployeeByUsername() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);

            Employee foundEmployee = employeeRepositoryImpl.findEmployeeByUsername(employee.username);

            Assertions.assertEquals(employee.toString(), foundEmployee.toString());
        }

    }

    @Nested
    class DeleteEmployee {

        @Test
        void shouldDeleteAEmployee() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);

            Assertions.assertDoesNotThrow(() -> employeeRepositoryImpl.deleteEmployee(employee.id));
            Assertions.assertNull(employeeRepositoryImpl.findEmployeeByUsername(employee.username));
        }
    }

    @Nested
    class UpdateIsActiveEmployeeAttribute {

        @Test
        void shouldUpdateTheIsActiveEmployeeAttribute() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);

            Assertions.assertDoesNotThrow(() -> employeeRepositoryImpl.updateIsActiveEmployeeAttribute(employee.id, false));
            Assertions.assertFalse(employeeRepositoryImpl.findEmployeeByUsername(employee.username).isActive);
        }
    }
}
