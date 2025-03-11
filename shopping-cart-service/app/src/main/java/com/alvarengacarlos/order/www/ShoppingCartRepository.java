package com.alvarengacarlos.order.www;

import java.util.List;
import java.util.UUID;

public interface ShoppingCartRepository {

    void addProduct(UUID customerId, UUID productId);

    void removeProduct(UUID customerId, UUID productId);

    List<UUID> findProducts(UUID customerId);
}
