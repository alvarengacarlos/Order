package com.alvarengacarlos.order.www;

import com.alvarengacarlos.www.control.App;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.lambda.LambdaClient;

public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final App app = new App();
    private final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();
    private final LambdaClient lambdaClient = LambdaClient.builder().build();
    private final Gson gson = new Gson();
    private final String routePrefix = "/order-service";

    public Handler() {
        createOrderRoute();
    }

    private void createOrderRoute() {
        String productMicroserviceFunctionName = System.getenv("PRODUCT_MICROSERVICE_FUNCTION_NAME");
        if (productMicroserviceFunctionName == null) {
            throw new RuntimeException("The 'PRODUCT_MICROSERVICE_FUNCTION_NAME' env cannot be empty");
        }
        OrderRepositoryImpl orderRepositoryImpl = new OrderRepositoryImpl(dynamoDbClient);
        ProductRepositoryImpl productRepositoryImpl = new ProductRepositoryImpl(lambdaClient, productMicroserviceFunctionName, gson);
        OrderService orderService = new OrderService(orderRepositoryImpl, productRepositoryImpl);
        OrderController orderController = new OrderController(orderService, gson);
        app.post(routePrefix + "/orders", request -> orderController.makeOrder(request));
        app.patch(routePrefix + "/orders/{orderId}", request -> orderController.changeOrderStatus(request));
        app.get(routePrefix + "/orders/{orderId}", request -> orderController.trackOrder(request));
        app.get(routePrefix + "/orders", request -> orderController.showOrders(request));
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
