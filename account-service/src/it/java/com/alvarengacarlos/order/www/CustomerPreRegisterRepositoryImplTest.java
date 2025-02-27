package com.alvarengacarlos.order.www;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class CustomerPreRegisterRepositoryImplTest {

    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbHelper dynamoDbHelper;
    private static CustomerPreRegisterRepositoryImpl customerPreRegisterRepositoryImpl;
    private final Faker faker = new Faker();
    private final String phoneNumber = faker.phoneNumber().cellPhone();

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        customerPreRegisterRepositoryImpl = new CustomerPreRegisterRepositoryImpl(dynamoDbClient);
    }

    @Nested
    class SaveCustomerPreRegister {

        @Test
        void shouldSaveACustomerPreRegister() {
            CustomerPreRegister customerPreRegister = CustomerPreRegister.newCustomerPreRegister(phoneNumber);

            Assertions.assertDoesNotThrow(() -> customerPreRegisterRepositoryImpl.saveCustomerPreRegister(customerPreRegister));
            Assertions.assertEquals(customerPreRegister.toString(), customerPreRegisterRepositoryImpl.findCustomerPreRegister(phoneNumber).toString());
        }
    }

    @Nested
    class SendSmsToCustomer {

        @Test
        void shouldSendASmsToACustomer() {
            //TODO: Implements
        }
    }

    @Nested
    class FindCustomerPreRegister {

        @Test
        void shouldReturnNull() {
            CustomerPreRegister foundCustomerPreRegister = customerPreRegisterRepositoryImpl.findCustomerPreRegister(phoneNumber);

            Assertions.assertNull(foundCustomerPreRegister);
        }

        @Test
        void shouldFindACustomerPreRegister() {
            CustomerPreRegister customerPreRegister = CustomerPreRegister.newCustomerPreRegister(phoneNumber);
            customerPreRegisterRepositoryImpl.saveCustomerPreRegister(customerPreRegister);

            CustomerPreRegister foundCustomerPreRegister = customerPreRegisterRepositoryImpl.findCustomerPreRegister(phoneNumber);

            Assertions.assertEquals(customerPreRegister.toString(), foundCustomerPreRegister.toString());
        }
    }
}
