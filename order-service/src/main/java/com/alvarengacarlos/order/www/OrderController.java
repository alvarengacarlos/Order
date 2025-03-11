package com.alvarengacarlos.order.www;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.alvarengacarlos.www.control.RequestBuilder;
import com.alvarengacarlos.www.control.ResponseBuilder;
import com.google.gson.Gson;

public class OrderController {

    private final OrderService orderService;
    private final Gson gson;
    private final Map<String, String> defaultHeaders = Map.of("Content-Type", "application/json");

    public OrderController(OrderService orderService, Gson gson) {
        this.orderService = orderService;
        this.gson = gson;
    }

    public ResponseBuilder.Response makeOrder(RequestBuilder.Request request) {
        try {
            MakeOrderDto makeOrderDto = gson.fromJson(request.body, MakeOrderDto.class);
            //TODO: Add validation
            orderService.makeOrder(makeOrderDto);
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(201)
                    .withBody(gson.toJson(Map.of("message", "Success")))
                    .build();
        } catch (ProductDoesNotExistException exception) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(404)
                    .withBody(gson.toJson(Map.of("message", exception.getMessage())))
                    .build();
        }
    }

    public ResponseBuilder.Response changeOrderStatus(RequestBuilder.Request request) {
        try {
            ChangeOrderStatusDto changeOrderStatusDto = gson.fromJson(request.body, ChangeOrderStatusDto.class);
            UUID orderId = UUID.fromString(request.pathParameters.get("orderId"));
            //TODO: Add validation
            orderService.changeOrderStatus(orderId, changeOrderStatusDto);
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(200)
                    .withBody(gson.toJson(Map.of("message", "Success")))
                    .build();
        } catch (OrderDoesNotExistException exception) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(404)
                    .withBody(gson.toJson(Map.of("message", exception.getMessage())))
                    .build();
        } catch (InvalidOrderStatusException exception) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(400)
                    .withBody(gson.toJson(Map.of("message", exception.getMessage())))
                    .build();
        }
    }

    public ResponseBuilder.Response trackOrder(RequestBuilder.Request request) {
        try {
            UUID orderId = UUID.fromString(request.pathParameters.get("orderId"));
            //TODO: Add validation
            Order order = orderService.trackOrder(orderId);
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(200)
                    .withBody(gson.toJson(order))
                    .build();
        } catch (OrderDoesNotExistException exception) {
            return new ResponseBuilder()
                    .withHeaders(defaultHeaders)
                    .withStatusCode(404)
                    .withBody(gson.toJson(Map.of("message", exception.getMessage())))
                    .build();
        }
    }

    public ResponseBuilder.Response showOrders(RequestBuilder.Request request) {
        UUID customerId = UUID.fromString(request.headers.get("customerId"));
        //TODO: Add validation
        List<Order> orders = orderService.showOrders(customerId);
        return new ResponseBuilder()
                .withHeaders(defaultHeaders)
                .withStatusCode(200)
                .withBody(gson.toJson(orders))
                .build();

    }
}
