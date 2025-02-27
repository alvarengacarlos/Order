package com.alvarengacarlos.order.www;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class CustomerPreRegisterRepositoryImpl implements CustomerPreRegisterRepository {

    private final String tableName = "Order";
    private final String partitionKeyPrefix = "Account#";
    private final String sortKey = "PreRegister";
    private final DynamoDbClient dynamoDbClient;

    public CustomerPreRegisterRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void saveCustomerPreRegister(CustomerPreRegister customerPreRegister) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + customerPreRegister.phoneNumber).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        item.put("validationCode", AttributeValue.builder().s(customerPreRegister.validationCode).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void sendSmsToCustomer(String phoneNumber, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CustomerPreRegister findCustomerPreRegister(String phoneNumber) {
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
        return new CustomerPreRegister(
                item.get("partitionKey").s().replace(partitionKeyPrefix, ""),
                item.get("validationCode").s()
        );
    }
}
