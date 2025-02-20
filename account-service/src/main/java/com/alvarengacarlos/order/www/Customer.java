package com.alvarengacarlos.order.www;

import java.util.UUID;

public record Customer(UUID id, String name, String phoneNumber) {}
