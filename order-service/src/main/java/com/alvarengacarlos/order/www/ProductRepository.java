package com.alvarengacarlos.order.www;

import java.util.UUID;

public interface ProductRepository {

    Product findProduct(UUID productId);

}
