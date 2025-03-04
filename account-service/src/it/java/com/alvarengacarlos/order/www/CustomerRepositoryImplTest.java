package com.alvarengacarlos.order.www;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class CustomerRepositoryImplTest {

    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbHelper dynamoDbHelper;
    private static CustomerRepositoryImpl customerRepositoryImpl;
    private final Faker faker = new Faker();
    private final String name = faker.name().name();
    private final String phoneNumber = faker.phoneNumber().cellPhone();

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        customerRepositoryImpl = new CustomerRepositoryImpl(dynamoDbClient);
    }

    @Test
    void shouldSaveACustomer() {
        Customer customer = new Customer(name, phoneNumber);
        Assertions.assertDoesNotThrow(() -> customerRepositoryImpl.saveCustomer(customer));
    }
}
