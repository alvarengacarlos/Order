package com.alvarengacarlos.order.www;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {

    public final UUID orderId;
    public final List<UUID> productIds;
    public final PaymentMethod paymentMethod;
    public final OrderStatus status;
    public final String address;
    public final BigDecimal total;
    public final UUID customerId;

    public Order(
            UUID orderId,
            List<UUID> productIds,
            PaymentMethod paymentMethod,
            OrderStatus status,
            String address,
            BigDecimal total,
            UUID customerId
    ) {
        this.orderId = orderId;
        this.productIds = Collections.unmodifiableList(productIds);
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.address = address;
        this.total = total;
        this.customerId = customerId;
    }

    public static Order newOrder(
            List<UUID> productIds,
            PaymentMethod paymentMethod,
            String address,
            BigDecimal total,
            UUID customerId
    ) {
        return new Order(UUID.randomUUID(), productIds, paymentMethod, OrderStatus.PENDING, address, total, customerId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order{");
        sb.append("orderId=").append(orderId);
        sb.append(", productIds=").append(productIds);
        sb.append(", paymentMethod=").append(paymentMethod);
        sb.append(", status=").append(status);
        sb.append(", address=").append(address);
        sb.append(", total=").append(total);
        sb.append(", customerId=").append(customerId);
        sb.append('}');
        return sb.toString();
    }
}
