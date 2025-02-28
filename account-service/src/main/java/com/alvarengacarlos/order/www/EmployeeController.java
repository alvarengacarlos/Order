package com.alvarengacarlos.order.www;

import java.util.Map;

import com.alvarengacarlos.www.control.RequestBuilder;
import com.alvarengacarlos.www.control.ResponseBuilder;
import com.google.gson.Gson;

public class EmployeeController {

    private final EmployeeService employeeService;
    private final Gson gson;
    private Map<String, String> defaultHeaders = Map.of("Content-Type", "application/json");

    public EmployeeController(EmployeeService employeeService, Gson gson) {
        this.employeeService = employeeService;
        this.gson = gson;
    }

    public ResponseBuilder.Response createEmployee(RequestBuilder.Request request) {
        try {
            CreateEmployeeDto createEmployeeDto = gson.fromJson(request.body, CreateEmployeeDto.class);
            //TODO: Add validation
            employeeService.createEmployee(createEmployeeDto);
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(201)
                    .withBody(gson.toJson(Map.of("message", "Success")))
                    .build();
        } catch (EmployeeExistsException e) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(400)
                    .withBody(gson.toJson(Map.of("message", "Employee exists")))
                    .build();
        }
    }
}
