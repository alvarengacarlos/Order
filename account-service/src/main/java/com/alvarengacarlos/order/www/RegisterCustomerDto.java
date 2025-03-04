package com.alvarengacarlos.order.www;

public record RegisterCustomerDto(
        String phoneNumber,
        String validationCode,
        String name) {

}
