package com.alvarengacarlos.order.www;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class CustomerPreRegistrationRepositoryImpl implements CustomerPreRegistrationRepository {

    private final String tableName = "Order";
    private final String partitionKeyPrefix = "Account#";
    private final String sortKey = "PreRegister";
    private final DynamoDbClient dynamoDbClient;

    public CustomerPreRegistrationRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void saveCustomerPreRegistration(CustomerPreRegistration customerPreRegistration) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + customerPreRegistration.phoneNumber).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        item.put("validationCode", AttributeValue.builder().s(customerPreRegistration.validationCode).build());
        Long expiresAt = customerPreRegistration.expiresAt.toEpochMilli() / 1000;
        item.put("expiresAt", AttributeValue.builder().s(expiresAt.toString()).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void sendSmsToCustomer(String phoneNumber, String text) {
        //TODO: Implements a real message sender
        System.out.println("Sending ...");
    }

    @Override
    public CustomerPreRegistration findCustomerPreRegistration(String phoneNumber) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + phoneNumber).build());
        key.put("sortKey", AttributeValue.builder().s(sortKey).build());
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();
        GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        if (getItemResponse.item().isEmpty()) {
            return null;
        }

        Map<String, AttributeValue> item = getItemResponse.item();
        return new CustomerPreRegistration(
                item.get("partitionKey").s().replace(partitionKeyPrefix, ""),
                item.get("validationCode").s(),
                Instant.ofEpochSecond(Long.parseLong(item.get("expiresAt").s()))
        );
    }
}
