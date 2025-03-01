package com.alvarengacarlos.order.www;

import java.util.Map;

import com.alvarengacarlos.www.control.RequestBuilder;
import com.alvarengacarlos.www.control.ResponseBuilder;
import com.google.gson.Gson;

public class CustomerController {

    private final CustomerService customerService;
    private final Gson gson;
    private Map<String, String> defaultHeaders = Map.of("Content-Type", "application/json");

    public CustomerController(CustomerService customerService, Gson gson) {
        this.customerService = customerService;
        this.gson = gson;
    }

    public ResponseBuilder.Response preRegisterCustomer(RequestBuilder.Request request) {
        PreRegisterCustomerDto preRegisterCustomerDto = gson.fromJson(request.body, PreRegisterCustomerDto.class);
        //TODO: Add validation
        customerService.preRegisterCustomer(preRegisterCustomerDto);
        return new ResponseBuilder()
                .withHeaders(defaultHeaders)
                .withStatusCode(201)
                .withBody(gson.toJson(Map.of("message", "Success")))
                .build();

    }

    public ResponseBuilder.Response registerCustomer(RequestBuilder.Request request) {
        try {
            RegisterCustomerDto registerCustomerDto = gson.fromJson(request.body, RegisterCustomerDto.class);
            //TODO: Add validation
            customerService.registerCustomer(registerCustomerDto);
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(201)
                    .withBody(gson.toJson(Map.of("message", "Success")))
                    .build();

        } catch (InvalidValidationCodeException exception) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(400)
                    .withBody(gson.toJson(Map.of("message", exception.getMessage())))
                    .build();
        }

    }
}
