package com.alvarengacarlos.order.www;

import java.util.List;
import java.util.UUID;

public record MakeOrderDto(
        List<UUID> productIds,
        PaymentMethod paymentMethod,
        String address,
        UUID customerId) {

}
