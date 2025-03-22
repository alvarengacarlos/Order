package com.alvarengacarlos.order.www;

import java.util.Map;
import java.util.UUID;

import com.alvarengacarlos.www.control.RequestBuilder;
import com.alvarengacarlos.www.control.ResponseBuilder;
import com.google.gson.Gson;

public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final Gson gson;
    private final Map<String, String> defaultHeaders = Map.of("Content-Type", "application/json");

    public ShoppingCartController(ShoppingCartService shoppingCartService, Gson gson) {
        this.shoppingCartService = shoppingCartService;
        this.gson = gson;
    }

    public ResponseBuilder.Response addProductToShoppingCart(RequestBuilder.Request request) {
        AddProductToShoppingCartDto addProductToShoppingCartDto = gson.fromJson(request.body, AddProductToShoppingCartDto.class);
        UUID customerId = UUID.fromString(request.headers.get("customerId"));
        //TODO: Add validation
        shoppingCartService.addProductToShoppingCart(customerId, addProductToShoppingCartDto);
        return new ResponseBuilder()
                .withHeaders(defaultHeaders)
                .withStatusCode(201)
                .withBody(gson.toJson(Map.of("message", "Success")))
                .build();

    }

    public ResponseBuilder.Response removeProductFromShoppingCart(RequestBuilder.Request request) {
        RemoveProductFromShoppingCartDto removeProductFromShoppingCartDto = gson.fromJson(request.body, RemoveProductFromShoppingCartDto.class);
        UUID customerId = UUID.fromString(request.headers.get("customerId"));
        //TODO: Add validation
        shoppingCartService.removeProductFromShoppingCart(customerId, removeProductFromShoppingCartDto);
        return new ResponseBuilder()
                .withHeaders(defaultHeaders)
                .withStatusCode(201)
                .withBody(gson.toJson(Map.of("message", "Success")))
                .build();
    }

    public ResponseBuilder.Response showShoppingCartProducts(RequestBuilder.Request request) {
        UUID customerId = UUID.fromString(request.headers.get("customerId"));
         //TODO: Add validationshoppingCartService.showShoppingCartProducts(customerId);
        return new ResponseBuilder()
        
                .withHeaders(defaultHeaders)
                .withStatusCode(201)
                .withBody(gson.toJson(Map.of("message", "Success")))
                .build();

    }
}
