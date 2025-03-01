package com.alvarengacarlos.order.www;

import com.alvarengacarlos.www.control.App;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final App app = new App();
    private final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
    private final Gson gson = new Gson();

    public Handler() {
        createEmployeeRoute();
        createCustomerRoute();
    }

    private void createEmployeeRoute() {
        String jwtSecret = System.getenv("JWT_SECRET");
        if (jwtSecret == null) {
            throw new RuntimeException("The 'JWT_SECRET' env cannot be empty");
        }
        EmployeeRepositoryImpl employeeRepositoryImpl = new EmployeeRepositoryImpl(dynamoDbClient);
        EmployeeService employeeService = new EmployeeService(employeeRepositoryImpl, jwtSecret);
        EmployeeController employeeController = new EmployeeController(employeeService, gson);
        app.post("/employees", request -> employeeController.createEmployee(request));
        app.delete("/employees/{employeeId}", request -> employeeController.destroyEmployee(request));
        app.patch("/employees/{employeeId}/activate", request -> employeeController.activateEmployee(request));
        app.patch("/employees/{employeeId}/deactivate", request -> employeeController.deactivateEmployee(request));
        app.post("/employees/authenticate", request -> employeeController.authenticateEmployee(request));
    }

    private void createCustomerRoute() {
        CustomerPreRegistrationRepositoryImpl customerPreRegisterRepositoryImpl = new CustomerPreRegistrationRepositoryImpl(dynamoDbClient);
        CustomerRepositoryImpl customerRepositoryImpl = new CustomerRepositoryImpl(dynamoDbClient);
        CustomerService customerService = new CustomerService(customerRepositoryImpl, customerPreRegisterRepositoryImpl);
        CustomerController customerController = new CustomerController(customerService, gson);
        app.post("/customers/pre-registration", request -> customerController.preRegisterCustomer(request));
        app.post("/customers/registration", request -> customerController.registerCustomer(request));
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        return app.dispatch(apiGatewayProxyRequestEvent);
    }
}
