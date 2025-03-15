package com.alvarengacarlos.order.www;

import java.util.UUID;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

public class ProductRepositoryImpl implements ProductRepository {

    private final LambdaClient lambdaClient;
    private final String productMicroserviceFunctionName;
    private final Gson gson;

    public ProductRepositoryImpl(LambdaClient lambdaClient, String productMicroserviceFunctionName, Gson gson) {
        this.lambdaClient = lambdaClient;
        this.productMicroserviceFunctionName = productMicroserviceFunctionName;
        this.gson = gson;
    }

    @Override
    public Product findProduct(UUID productId) {
        APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = new APIGatewayProxyRequestEvent()
                .withResource("/{proxy+}")
                .withVersion("1.0")
                .withHttpMethod("GET")
                .withPath("/product-service/products");
        SdkBytes sdkBytes = SdkBytes.fromUtf8String(gson.toJson(apiGatewayProxyRequestEvent));
        InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(productMicroserviceFunctionName)
                .invocationType(InvocationType.REQUEST_RESPONSE)
                .payload(sdkBytes)
                .build();
        InvokeResponse invokeResponse = lambdaClient.invoke(invokeRequest);

        String payload = invokeResponse.payload().asUtf8String();
        //TODO: Check the payload and remove this log
        System.out.println("PAYLOAD###-> " + payload);
        APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = gson.fromJson(payload, APIGatewayProxyResponseEvent.class);
        if (apiGatewayProxyResponseEvent.getStatusCode() == 404) {
            return null;
        }

        return gson.fromJson(apiGatewayProxyResponseEvent.getBody(), Product.class);
    }

}
