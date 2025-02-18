package com.alvarengacarlos.order.www;

public record CreateEmployeeDto(
        String name,
        String username,
        String password,
        Role role) {

}
