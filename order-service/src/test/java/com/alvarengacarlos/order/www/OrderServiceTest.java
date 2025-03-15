package com.alvarengacarlos.order.www;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import com.github.javafaker.Faker;

public class OrderServiceTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private final OrderService orderService = new OrderService(orderRepository, productRepository);
    private final Faker faker = new Faker();
    private final List<UUID> productIds = List.of(UUID.randomUUID());
    private final UUID orderId = UUID.randomUUID();
    private final PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
    private final String address = faker.address().fullAddress();
    private final BigDecimal total = BigDecimal.valueOf(10.0);
    private final UUID customerId = UUID.randomUUID();

    @Nested
    class MakeOrder {

        @Test
        void shouldMakeAOrder() {
            PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
            String address = faker.address().fullAddress();
            Product product = new Product(productIds.getFirst(), BigDecimal.valueOf(10.0));
            Mockito.when(productRepository.findProduct(product.productId())).thenReturn(product);
            MakeOrderDto makeOrderDto = new MakeOrderDto(productIds, paymentMethod, address, customerId);

            Assertions.assertDoesNotThrow(() -> orderService.makeOrder(makeOrderDto));

            Mockito.verify(productRepository, Mockito.times(1)).findProduct(product.productId());
            Mockito.verify(
                    orderRepository,
                    Mockito.times(1)
            ).saveOrder(Mockito.any(Order.class));
            Mockito.verify(
                    orderRepository,
                    Mockito.times(1)
            ).saveOrder(Mockito.argThat((order) -> order.total.equals(product.price())));
        }
    }

    @Nested
    class ChangeOrderStatus {

        @Nested
        class ShouldThrowInvalidOrderStatusException {

            @Test
            void whenCurrentOrderStatusIsPending() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.PENDING, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.PENDING))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }

            @Test
            void whenCurrentOrderStatusIsAccepted() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.ACCEPTED, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.ACCEPTED))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }

            @Test
            void whenCurrentOrderStatusIsRejected() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.REJECTED, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.REJECTED))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }

            @Test
            void whenCurrentOrderStatusIsCooking() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.COOKING, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.COOKING))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }

            @Test
            void whenCurrentOrderStatusIsReady() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.READY, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.READY))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }

            @Test
            void whenCurrentOrderStatusIsDelivering() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.DELIVERING, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.DELIVERING))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }

            @Test
            void whenCurrentOrderStatusIsPayed() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.PAYED, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.PAYED))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }

            @Test
            void whenCurrentOrderStatusIsDelivered() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.DELIVERED, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertThrows(
                        InvalidOrderStatusException.class,
                        () -> orderService.changeOrderStatus(orderId, new ChangeOrderStatusDto(OrderStatus.DELIVERED))
                );

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
            }
        }

        @Nested
        class ShouldChangeAOrderStatus {

            @ParameterizedTest
            @EnumSource(names = {"ACCEPTED", "REJECTED"})
            void whenCurrentOrderStatusIsPending(OrderStatus orderStatus) {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.PENDING, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertDoesNotThrow(() -> orderService.changeOrderStatus(
                        orderId,
                        new ChangeOrderStatusDto(orderStatus)
                ));

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).updateOrderStatus(orderId, Mockito.any(OrderStatus.class));
            }

            @Test
            void whenCurrentOrderStatusIsAccepted() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.ACCEPTED, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertDoesNotThrow(() -> orderService.changeOrderStatus(
                        orderId,
                        new ChangeOrderStatusDto(OrderStatus.COOKING)
                ));

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).updateOrderStatus(orderId, Mockito.any(OrderStatus.class));
            }

            @Test
            void whenCurrentOrderStatusIsCooking() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.COOKING, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertDoesNotThrow(() -> orderService.changeOrderStatus(
                        orderId,
                        new ChangeOrderStatusDto(OrderStatus.READY)
                ));

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).updateOrderStatus(orderId, Mockito.any(OrderStatus.class));
            }

            @Test
            void whenCurrentOrderStatusIsReady() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.READY, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertDoesNotThrow(() -> orderService.changeOrderStatus(
                        orderId,
                        new ChangeOrderStatusDto(OrderStatus.DELIVERING)
                ));

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).updateOrderStatus(orderId, Mockito.any(OrderStatus.class));
            }

            @Test
            void whenCurrentOrderStatusIsDelivering() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.DELIVERING, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertDoesNotThrow(() -> orderService.changeOrderStatus(
                        orderId,
                        new ChangeOrderStatusDto(OrderStatus.PAYED)
                ));

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).updateOrderStatus(orderId, Mockito.any(OrderStatus.class));
            }

            @Test
            void whenCurrentOrderStatusIsPayed() {
                Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.PAYED, address, total, customerId);
                Mockito.when(
                        orderRepository.findOrder(orderId)
                ).thenReturn(order);

                Assertions.assertDoesNotThrow(() -> orderService.changeOrderStatus(
                        orderId,
                        new ChangeOrderStatusDto(OrderStatus.DELIVERED)
                ));

                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).findOrder(orderId);
                Mockito.verify(
                        orderRepository,
                        Mockito.times(1)
                ).updateOrderStatus(orderId, Mockito.any(OrderStatus.class));
            }
        }
    }

    @Nested
    class TrackOrder {

        @Test
        void shouldThrowOrderDoesNotExistException() {
            Mockito.when(
                    orderRepository.findOrder(orderId)
            ).thenReturn(null);

            Assertions.assertThrows(OrderDoesNotExistException.class, () -> orderService.trackOrder(orderId));

            Mockito.verify(
                    orderRepository,
                    Mockito.times(1)
            ).findOrder(orderId);
        }

        @Test
        void shouldTrackAOrder() throws OrderDoesNotExistException {
            Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.PAYED, address, total, customerId);
            Mockito.when(
                    orderRepository.findOrder(orderId)
            ).thenReturn(order);

            Order trackedOrder = orderService.trackOrder(orderId);

            Mockito.verify(
                    orderRepository,
                    Mockito.times(1)
            ).findOrder(orderId);
            Assertions.assertEquals(order.toString(), trackedOrder.toString());
        }
    }

    @Test
    void shouldShowOrders() {
        Order order = new Order(orderId, productIds, paymentMethod, OrderStatus.PAYED, address, total, customerId);
        Mockito.when(
                orderRepository.findOrders(customerId)
        ).thenReturn(List.of(order));

        List<Order> orders = orderService.showOrders(customerId);

        Mockito.verify(
                orderRepository,
                Mockito.times(1)
        ).findOrders(customerId);
        Assertions.assertEquals(1, orders.size());
        Assertions.assertEquals(order.toString(), orders.getFirst().toString());
    }

}
