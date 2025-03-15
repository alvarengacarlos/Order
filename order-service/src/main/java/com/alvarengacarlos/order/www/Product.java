package com.alvarengacarlos.order.www;

import java.math.BigDecimal;
import java.util.UUID;

public record Product(
        UUID productId,
        BigDecimal price) {

}
