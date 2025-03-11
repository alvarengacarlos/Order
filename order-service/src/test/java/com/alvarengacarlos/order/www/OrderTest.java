package com.alvarengacarlos.order.www;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.alvarengacarlos.www.control.RequestBuilder;
import com.alvarengacarlos.www.control.ResponseBuilder;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.javafaker.Faker;
import com.google.gson.Gson;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

public class OrderTest {

    private static LambdaClient lambdaClient = Mockito.mock(LambdaClient.class);
    private static OrderRepositoryImpl orderRepositoryImpl;
    private static OrderController orderController;
    private static DynamoDbHelper dynamoDbHelper;
    private static Gson gson = new Gson();
    private final Faker faker = new Faker();
    private final UUID productId = UUID.randomUUID();
    private final String paymentMethod = PaymentMethod.MONEY.toString();
    private final String address = faker.address().fullAddress();
    private final String customerId = UUID.randomUUID().toString();

    @BeforeAll
    static void beforeAll() {
        DynamoDbContainerSingleton dynamoDbContainerSingleton = DynamoDbContainerSingleton.getInstance();
        Integer port = dynamoDbContainerSingleton.startContainer();
        dynamoDbHelper = new DynamoDbHelper(port);
        DynamoDbClient dynamoDbClient = dynamoDbHelper.getClient();
        dynamoDbHelper.createOrderTable();
        orderRepositoryImpl = new OrderRepositoryImpl(dynamoDbClient);
        ProductRepositoryImpl productRepositoryImpl = new ProductRepositoryImpl(lambdaClient, "productmicroservicefunctionname", gson);
        OrderService orderService = new OrderService(orderRepositoryImpl, productRepositoryImpl);
        orderController = new OrderController(orderService, gson);
    }

    @Nested
    class MakeOrder {

        private final RequestBuilder.Request request = new RequestBuilder()
                .withBody(gson.toJson(Map.of(
                        "productIds", List.of(productId),
                        "paymentMethod", paymentMethod,
                        "address", address,
                        "customerId", customerId
                )))
                .build();

        @Test
        void sholdReturnNotFoundHttpResponse() {
            APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent().withStatusCode(404);
            InvokeResponse invokeResponse = InvokeResponse.builder().payload(SdkBytes.fromUtf8String(gson.toJson(apiGatewayProxyResponseEvent))).build();
            Mockito.when(lambdaClient.invoke(Mockito.any(InvokeRequest.class))).thenReturn(invokeResponse);

            ResponseBuilder.Response response = orderController.makeOrder(request);

            Assertions.assertEquals(404, response.statusCode);
        }

        @Test
        void shouldReturnCreatedHttpResponse() {
            APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(gson.toJson(Map.of(
                            "productId", productId.toString(),
                            "price", "10.0"
                    )));
            InvokeResponse invokeResponse = InvokeResponse.builder().payload(SdkBytes.fromUtf8String(gson.toJson(apiGatewayProxyResponseEvent))).build();
            Mockito.when(lambdaClient.invoke(Mockito.any(InvokeRequest.class))).thenReturn(invokeResponse);

            ResponseBuilder.Response response = orderController.makeOrder(request);

            Assertions.assertEquals(201, response.statusCode);
        }

    }

    @Nested
    class ChangeOrderStatus {

        @Test
        void shouldReturnNotFoundHttpResponse() {
            dynamoDbHelper.truncateOrderTable();
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(Map.of("orderId", UUID.randomUUID().toString()))
                    .withBody(gson.toJson(Map.of(
                            "orderStatus", "ACCEPTED"
                    )))
                    .build();

            ResponseBuilder.Response response = orderController.changeOrderStatus(request);

            Assertions.assertEquals(404, response.statusCode);
        }

        @Test
        void shouldReturnBadRequestHttpResponse() {
            Order order = Order.newOrder(List.of(productId), PaymentMethod.MONEY, faker.address().fullAddress(), new BigDecimal(10.0), UUID.randomUUID());
            orderRepositoryImpl.saveOrder(order);
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(Map.of("orderId", order.orderId.toString()))
                    .withBody(gson.toJson(Map.of(
                            "orderStatus", "PENDING"
                    )))
                    .build();

            ResponseBuilder.Response response = orderController.changeOrderStatus(request);

            Assertions.assertEquals(400, response.statusCode);
        }

        @Test
        void shouldReturnOkHttpResponse() {
            Order order = Order.newOrder(List.of(productId), PaymentMethod.MONEY, faker.address().fullAddress(), new BigDecimal(10.0), UUID.randomUUID());
            orderRepositoryImpl.saveOrder(order);
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(Map.of("orderId", order.orderId.toString()))
                    .withBody(gson.toJson(Map.of(
                            "orderStatus", "ACCEPTED"
                    )))
                    .build();

            ResponseBuilder.Response response = orderController.changeOrderStatus(request);

            Assertions.assertEquals(200, response.statusCode);
        }
    }

    @Nested
    class TrackOrder {

        @Test
        void shouldReturnNotFoundHttpResponse() {
            dynamoDbHelper.truncateOrderTable();
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(Map.of("orderId", UUID.randomUUID().toString()))
                    .build();

            ResponseBuilder.Response response = orderController.trackOrder(request);

            Assertions.assertEquals(404, response.statusCode);
        }

        @Test
        void shouldReturnOkHttpResponse() {
            Order order = Order.newOrder(List.of(productId), PaymentMethod.MONEY, faker.address().fullAddress(), new BigDecimal(10.0), UUID.randomUUID());
            orderRepositoryImpl.saveOrder(order);
            RequestBuilder.Request request = new RequestBuilder()
                    .withPathParameters(Map.of("orderId", order.orderId.toString()))
                    .build();

            ResponseBuilder.Response response = orderController.trackOrder(request);

            Assertions.assertEquals(200, response.statusCode);
        }
    }

    @Nested
    class ShowOrders {

        @Test
        void shouldReturnOkHttpResponse() {
            RequestBuilder.Request request = new RequestBuilder()
                    .withHeaders(Map.of("customerId", UUID.randomUUID().toString()))
                    .build();

            ResponseBuilder.Response response = orderController.showOrders(request);

            Assertions.assertEquals(200, response.statusCode);
        }
    }
}
