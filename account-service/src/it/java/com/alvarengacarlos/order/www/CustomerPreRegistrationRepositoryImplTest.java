package com.alvarengacarlos.order.www;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class CustomerPreRegistrationRepositoryImplTest {

    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbHelper dynamoDbHelper;
    private static CustomerPreRegistrationRepositoryImpl customerPreRegisterRepositoryImpl;
    private final Faker faker = new Faker();
    private final String phoneNumber = faker.phoneNumber().cellPhone();

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        customerPreRegisterRepositoryImpl = new CustomerPreRegistrationRepositoryImpl(dynamoDbClient);
    }

    @Nested
    class SaveCustomerPreRegister {

        @Test
        void shouldSaveACustomerPreRegister() {
            CustomerPreRegistration customerPreRegister = CustomerPreRegistration.newCustomerPreRegister(phoneNumber);

            Assertions.assertDoesNotThrow(() -> customerPreRegisterRepositoryImpl.saveCustomerPreRegistration(customerPreRegister));
            Assertions.assertEquals(customerPreRegister.toString(), customerPreRegisterRepositoryImpl.findCustomerPreRegistration(phoneNumber).toString());
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
            CustomerPreRegistration foundCustomerPreRegister = customerPreRegisterRepositoryImpl.findCustomerPreRegistration(phoneNumber);

            Assertions.assertNull(foundCustomerPreRegister);
        }

        @Test
        void shouldFindACustomerPreRegister() {
            CustomerPreRegistration customerPreRegistration = CustomerPreRegistration.newCustomerPreRegister(phoneNumber);
            customerPreRegisterRepositoryImpl.saveCustomerPreRegistration(customerPreRegistration);

            CustomerPreRegistration foundCustomerPreRegistration = customerPreRegisterRepositoryImpl.findCustomerPreRegistration(phoneNumber);

            Assertions.assertEquals(customerPreRegistration.toString(), foundCustomerPreRegistration.toString());
        }
    }
}
