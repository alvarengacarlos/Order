package com.alvarengacarlos.order.www;

import java.util.Map;
import java.util.UUID;

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
        } catch (EmployeeExistsException exception) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(400)
                    .withBody(gson.toJson(Map.of("message", exception.getMessage())))
                    .build();
        }
    }

    public ResponseBuilder.Response destroyEmployee(RequestBuilder.Request request) {
        UUID employeeId = UUID.fromString(request.pathParameters.get("employeeId"));
        //TODO: Add validation
        employeeService.destroyEmployee(employeeId);
        return new ResponseBuilder()
                .withHeaders(defaultHeaders)
                .withStatusCode(204)
                .withBody(gson.toJson(Map.of("message", "Success")))
                .build();
    }

    public ResponseBuilder.Response activateEmployee(RequestBuilder.Request request) {
        UUID employeeId = UUID.fromString(request.pathParameters.get("employeeId"));
        //TODO: Add validation
        employeeService.activateEmployee(employeeId);
        return new ResponseBuilder()
                .withHeaders(defaultHeaders)
                .withStatusCode(200)
                .withBody(gson.toJson(Map.of("message", "Success")))
                .build();
    }

    public ResponseBuilder.Response deactivateEmployee(RequestBuilder.Request request) {
        UUID employeeId = UUID.fromString(request.pathParameters.get("employeeId"));
        //TODO: Add validation
        employeeService.deactivateEmployee(employeeId);
        return new ResponseBuilder()
                .withHeaders(defaultHeaders)
                .withStatusCode(200)
                .withBody(gson.toJson(Map.of("message", "Success")))
                .build();
    }

    public ResponseBuilder.Response authenticateEmployee(RequestBuilder.Request request) {
        try {
            AuthenticateEmployeeDto authenticateEmployeeDto = gson.fromJson(request.body, AuthenticateEmployeeDto.class);
            //TODO: Add validation
            String token = employeeService.authenticateEmployee(authenticateEmployeeDto);
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(200)
                    .withBody(gson.toJson(Map.of("bearerToken", token)))
                    .build();

        } catch (AuthenticationFailureException exception) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(400)
                    .withBody(gson.toJson(Map.of("message", exception.getMessage())))
                    .build();
        }
    }
}
