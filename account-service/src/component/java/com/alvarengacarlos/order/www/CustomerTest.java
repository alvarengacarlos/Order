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

public class CustomerTest {

    private static CustomerPreRegistrationRepositoryImpl customerPreRegistrationRepositoryImpl;
    private static CustomerController customerController;
    private static DynamoDbHelper dynamoDbHelper;
    private static Gson gson = new Gson();
    private final Faker faker = new Faker();
    private final String phoneNumber = faker.phoneNumber().phoneNumber();

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        DynamoDbClient dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        CustomerRepositoryImpl customerRepositoryImpl = new CustomerRepositoryImpl(dynamoDbClient);
        customerPreRegistrationRepositoryImpl = new CustomerPreRegistrationRepositoryImpl(dynamoDbClient);
        CustomerService customerService = new CustomerService(customerRepositoryImpl, customerPreRegistrationRepositoryImpl);
        customerController = new CustomerController(customerService, gson);
    }

    @Test
    void shouldReturnCreatedHttpResponse() {
        Map<String, String> body = new HashMap<>();
        body.put("phoneNumber", phoneNumber);
        RequestBuilder.Request request = new RequestBuilder()
                .withBody(gson.toJson(body))
                .build();

        ResponseBuilder.Response response = customerController.preRegisterCustomer(request);

        Assertions.assertEquals(201, response.statusCode);
    }

    @Nested
    class RegisterCustomer {

        @Test
        void shouldReturnBadRequestHttpResponse() {
            CustomerPreRegistration customerPreRegistration = CustomerPreRegistration.newCustomerPreRegister(phoneNumber);
            String name = faker.name().name();
            Map<String, String> body = new HashMap<>();
            body.put("phoneNumber", phoneNumber);
            body.put("validationCode", customerPreRegistration.validationCode);
            body.put("name", name);
            RequestBuilder.Request request = new RequestBuilder()
                    .withBody(gson.toJson(body))
                    .build();

            ResponseBuilder.Response response = customerController.registerCustomer(request);

            Assertions.assertEquals(400, response.statusCode);
        }

        @Test
        void shouldReturnCreatedHttpResponse() {
            CustomerPreRegistration customerPreRegistration = CustomerPreRegistration.newCustomerPreRegister(phoneNumber);
            customerPreRegistrationRepositoryImpl.saveCustomerPreRegistration(customerPreRegistration);
            String name = faker.name().name();
            Map<String, String> body = new HashMap<>();
            body.put("phoneNumber", phoneNumber);
            body.put("validationCode", customerPreRegistration.validationCode);
            body.put("name", name);
            RequestBuilder.Request request = new RequestBuilder()
                    .withBody(gson.toJson(body))
                    .build();

            ResponseBuilder.Response response = customerController.registerCustomer(request);

            Assertions.assertEquals(201, response.statusCode);
        }
    }
}
