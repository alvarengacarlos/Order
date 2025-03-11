package com.alvarengacarlos.order.www;

import java.util.List;
import java.util.UUID;

public interface OrderRepository {

    void saveOrder(Order order);

    Order findOrder(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatus orderStatus);

    List<Order> findOrders(UUID customerId);
}
