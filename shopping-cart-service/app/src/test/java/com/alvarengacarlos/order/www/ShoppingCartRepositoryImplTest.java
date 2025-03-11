package com.alvarengacarlos.order.www;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class ShoppingCartRepositoryImplTest {

    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbHelper dynamoDbHelper;
    private static ShoppingCartRepositoryImpl shoppingCartRepositoryImpl;
    private final UUID productId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        shoppingCartRepositoryImpl = new ShoppingCartRepositoryImpl(dynamoDbClient);
    }

    @BeforeEach
    void beforeEach() {
        dynamoDbHelper.truncateOrderTable();
    }

    @Nested
    class AddProduct {

        @Nested
        class ShouldAddAProduct {

            @Test
            void whenThereIsNoShoppingCart() {
                shoppingCartRepositoryImpl.addProduct(customerId, productId);

                Assertions.assertEquals(1, shoppingCartRepositoryImpl.findProducts(customerId).size());
            }

            @Test
            void whenThereIsShoppingCart() {
                shoppingCartRepositoryImpl.addProduct(customerId, productId);

                shoppingCartRepositoryImpl.addProduct(customerId, productId);

                Assertions.assertEquals(2, shoppingCartRepositoryImpl.findProducts(customerId).size());
            }
        }

    }

    @Nested
    class RemoveProduct {

        @Nested
        class ShouldRemoveAProduct {

            @Test
            void whenThereIsNoShoppingCart() {
                shoppingCartRepositoryImpl.removeProduct(customerId, productId);

                Assertions.assertEquals(0, shoppingCartRepositoryImpl.findProducts(customerId).size());
            }

            @Test
            void whenThereIsShoppingCart() {
                shoppingCartRepositoryImpl.addProduct(customerId, productId);

                shoppingCartRepositoryImpl.removeProduct(customerId, productId);

                Assertions.assertEquals(0, shoppingCartRepositoryImpl.findProducts(customerId).size());
            }

            @Test
            void whenThereIsShoppingCartAndTwoProducts() {
                shoppingCartRepositoryImpl.addProduct(customerId, productId);
                shoppingCartRepositoryImpl.addProduct(customerId, productId);

                shoppingCartRepositoryImpl.removeProduct(customerId, productId);

                Assertions.assertEquals(1, shoppingCartRepositoryImpl.findProducts(customerId).size());
            }
        }

    }

    @Nested
    class FindProducts {

        @Test
        void shouldNotFoundProducts() {
            Assertions.assertEquals(0, shoppingCartRepositoryImpl.findProducts(customerId).size());
        }

        @Test
        void shouldFoundOneProducts() {
            shoppingCartRepositoryImpl.addProduct(customerId, productId);

            Assertions.assertEquals(1, shoppingCartRepositoryImpl.findProducts(customerId).size());
        }

    }

}
