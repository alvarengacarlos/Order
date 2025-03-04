package com.alvarengacarlos.order.www;

public class AuthenticateEmployeeDto {

    public final String username;
    public final String password;

    public AuthenticateEmployeeDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
