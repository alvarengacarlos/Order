package com.alvarengacarlos.order.www;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final Map<OrderStatus, Set<OrderStatus>> orderStatusTransition = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.ACCEPTED, OrderStatus.REJECTED),
            OrderStatus.ACCEPTED, Set.of(OrderStatus.COOKING),
            OrderStatus.COOKING, Set.of(OrderStatus.READY),
            OrderStatus.READY, Set.of(OrderStatus.DELIVERING),
            OrderStatus.DELIVERING, Set.of(OrderStatus.PAYED),
            OrderStatus.PAYED, Set.of(OrderStatus.DELIVERED)
    );

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public void makeOrder(MakeOrderDto makeOrderDto) throws ProductDoesNotExistException {
        List<Product> products = makeOrderDto.productIds().stream()
                .map(productRepository::findProduct)
                .filter(Objects::nonNull)
                .toList();
        if (products.size() != makeOrderDto.productIds().size()) {
            throw new ProductDoesNotExistException();
        }

        BigDecimal total = products.stream()
                .map(Product::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.newOrder(
                makeOrderDto.productIds(),
                makeOrderDto.paymentMethod(),
                makeOrderDto.address(),
                total, makeOrderDto.customerId()
        );
        this.orderRepository.saveOrder(order);
    }

    public void changeOrderStatus(UUID orderId, ChangeOrderStatusDto changeOrderStatusDto) throws OrderDoesNotExistException, InvalidOrderStatusException {
        Order order = this.orderRepository.findOrder(orderId);
        if (order == null) {
            throw new OrderDoesNotExistException();
        }

        if (order.status == OrderStatus.REJECTED
                || order.status == OrderStatus.DELIVERED
                || !orderStatusTransition.get(order.status).contains(changeOrderStatusDto.orderStatus())) {
            throw new InvalidOrderStatusException();
        }

        this.orderRepository.updateOrderStatus(order.orderId, changeOrderStatusDto.orderStatus());
    }

    public Order trackOrder(UUID orderId) throws OrderDoesNotExistException {
        Order order = this.orderRepository.findOrder(orderId);
        if (order == null) {
            throw new OrderDoesNotExistException();
        }

        return order;
    }

    public List<Order> showOrders(UUID customerId) {
        return this.orderRepository.findOrders(customerId);
    }
}
