package com.alvarengacarlos.order.www;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.alvarengacarlos.www.control.RequestBuilder;
import com.alvarengacarlos.www.control.ResponseBuilder;
import com.github.javafaker.Faker;
import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class EmployeeTest {

    private static EmployeeRepositoryImpl employeeRepositoryImpl;
    private static EmployeeController employeeController;
    private static DynamoDbHelper dynamoDbHelper;
    private static Gson gson = new Gson();
    private final Faker faker = new Faker();
    private final String name = faker.name().name();
    private final String username = faker.name().username();
    private final String password = faker.internet().password();
    private final EmployeeRole role = EmployeeRole.COOK;

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        DynamoDbClient dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        employeeRepositoryImpl = new EmployeeRepositoryImpl(dynamoDbClient);
        EmployeeService employeeService = new EmployeeService(employeeRepositoryImpl, "mysecret");
        employeeController = new EmployeeController(employeeService, gson);
    }

    @Nested
    class CreateEmployee {

        @Test
        void shouldReturnBadRequestHttpResponse() {
            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("username", username);
            body.put("password", password);
            body.put("employeeRole", role.toString());
            RequestBuilder.Request request = new RequestBuilder()
                    .withBody(gson.toJson(body))
                    .build();
            employeeRepositoryImpl.saveEmployee(Employee.newEmployee(name, username, password, role));

            ResponseBuilder.Response response = employeeController.createEmployee(request);

            Assertions.assertEquals(400, response.statusCode);
        }

        @Test
        void shouldReturnCreatedHttpResponse() {
            Map<String, String> body = new HashMap<>();
            body.put("name", name);
            body.put("username", username);
            body.put("password", password);
            body.put("employeeRole", role.toString());
            RequestBuilder.Request request = new RequestBuilder()
                    .withBody(gson.toJson(body))
                    .build();

            ResponseBuilder.Response response = employeeController.createEmployee(request);

            Assertions.assertEquals(201, response.statusCode);
        }
    }

    @Nested
    class DestroyEmployee {

        @Test
        void shouldReturnNoContentHttpResponse() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);
            Map<String, String> pathParameters = new HashMap<>();
            pathParameters.put("employeeId", employee.id.toString());
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(pathParameters)
                    .build();

            ResponseBuilder.Response response = employeeController.destroyEmployee(request);

            Assertions.assertEquals(204, response.statusCode);
        }
    }

    @Nested
    class ActivateEmployee {

        @Test
        void shouldReturnOkHttpResponse() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);
            Map<String, String> pathParameters = new HashMap<>();
            pathParameters.put("employeeId", employee.id.toString());
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(pathParameters)
                    .build();

            ResponseBuilder.Response response = employeeController.activateEmployee(request);

            Assertions.assertEquals(200, response.statusCode);
        }
    }

    @Nested
    class DeactivateEmployee {

        @Test
        void shouldReturnOkHttpResponse() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);
            Map<String, String> pathParameters = new HashMap<>();
            pathParameters.put("employeeId", employee.id.toString());
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(pathParameters)
                    .build();

            ResponseBuilder.Response response = employeeController.deactivateEmployee(request);

            Assertions.assertEquals(200, response.statusCode);
        }
    }

    @Nested
    class AuthenticateEmployee {

        @Test
        void shouldReturnBadRequestHttpResponse() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);
            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("password", "1234");
            RequestBuilder.Request request = new RequestBuilder()
                    .withBody(gson.toJson(body))
                    .build();

            ResponseBuilder.Response response = employeeController.authenticateEmployee(request);

            Assertions.assertEquals(400, response.statusCode);
        }

        @Test
        void shouldReturnOkHttpResponse() {
            Employee employee = Employee.newEmployee(name, username, password, role);
            employeeRepositoryImpl.saveEmployee(employee);
            Map<String, String> body = new HashMap<>();
            body.put("username", username);
            body.put("password", password);
            RequestBuilder.Request request = new RequestBuilder()
                    .withBody(gson.toJson(body))
                    .build();

            ResponseBuilder.Response response = employeeController.authenticateEmployee(request);

            Assertions.assertEquals(200, response.statusCode);
            Assertions.assertTrue(response.body.split(":")[0].contains("bearerToken"));
            Assertions.assertFalse(response.body.split(":")[1].isEmpty());

        }
    }
}
