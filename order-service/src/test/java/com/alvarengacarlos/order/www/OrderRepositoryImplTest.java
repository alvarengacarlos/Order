package com.alvarengacarlos.order.www;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class OrderRepositoryImplTest {

    private static DynamoDbClient dynamoDbClient;
    private static OrderRepositoryImpl orderRepositoryImpl;
    private static DynamoDbHelper dynamoDbHelper;
    private final Faker faker = new Faker();
    private final List<UUID> productIds = List.of(UUID.randomUUID());
    private final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    private final String address = faker.address().fullAddress();
    private final BigDecimal total = BigDecimal.valueOf(10.0);
    private final UUID customerId = UUID.randomUUID();

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        orderRepositoryImpl = new OrderRepositoryImpl(dynamoDbClient);
    }

    @Nested
    class SaveOrder {

        @Test
        void shouldSaveAOrder() {
            Order order = Order.newOrder(productIds, paymentMethod, address, total, customerId);

            orderRepositoryImpl.saveOrder(order);

            Assertions.assertEquals(order.toString(), orderRepositoryImpl.findOrder(order.orderId).toString());
        }

    }

    @Nested
    class FindOrder {

        @Test
        void shouldNotFoundAOrder() {
            Order order = Order.newOrder(productIds, paymentMethod, address, total, customerId);

            Order foundOrder = orderRepositoryImpl.findOrder(order.orderId);

            Assertions.assertNull(foundOrder);
        }

        @Test
        void shouldFoundAOrder() {
            Order order = Order.newOrder(productIds, paymentMethod, address, total, customerId);
            orderRepositoryImpl.saveOrder(order);

            Order foundOrder = orderRepositoryImpl.findOrder(order.orderId);

            Assertions.assertEquals(order.toString(), foundOrder.toString());
        }
    }

    @Nested
    class UpdateOrderStatus {

        @Test
        void shouldUpdateAOrderStatus() {
            Order order = Order.newOrder(productIds, paymentMethod, address, total, customerId);
            orderRepositoryImpl.saveOrder(order);

            orderRepositoryImpl.updateOrderStatus(order.orderId, OrderStatus.ACCEPTED);

            Assertions.assertEquals(OrderStatus.ACCEPTED, orderRepositoryImpl.findOrder(order.orderId).status);
        }
    }

    @Nested
    class FindOrders {

        @Test
        void shouldNotFoundOrders() {
            dynamoDbHelper.truncateOrderTable();

            List<Order> orders = orderRepositoryImpl.findOrders(customerId);

            Assertions.assertTrue(orders.isEmpty());
        }

        @Test
        void shouldFoundOrders() {
            Order order = Order.newOrder(productIds, paymentMethod, address, total, customerId);
            orderRepositoryImpl.saveOrder(order);

            List<Order> orders = orderRepositoryImpl.findOrders(customerId);

            Assertions.assertFalse(orders.isEmpty());
        }
    }
}
