package com.alvarengacarlos.order.www;

import com.alvarengacarlos.www.control.App;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final App app = new App();
    private final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
    private final Gson gson = new Gson();
    private final String routePrefix = "/shopping-cart-service";

    public Handler() {
        createShoppingCartRoute();
    }

    private void createShoppingCartRoute() {
        ShoppingCartRepositoryImpl shoppingCartRepositoryImpl = new ShoppingCartRepositoryImpl(dynamoDbClient);
        ShoppingCartService shoppingCartService = new ShoppingCartService(shoppingCartRepositoryImpl);
        ShoppingCartController shoppingCartController = new ShoppingCartController(shoppingCartService, gson);
        app.post(routePrefix + "/add-product", request -> shoppingCartController.addProductToShoppingCart(request));
        app.post(routePrefix + "/remove-product", request -> shoppingCartController.removeProductFromShoppingCart(request));
        app.get(routePrefix + "/products", request -> shoppingCartController.showShoppingCartProducts(request));
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {
        try {
            return app.dispatch(apiGatewayProxyRequestEvent);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500);
        }
    }
}
