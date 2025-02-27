package com.alvarengacarlos.order.www;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class CustomerRepositoryImpl implements CustomerRepository {

    private final String tableName = "Order";
    private final String partitionKeyPrefix = "Account#";
    private final String sortKey = "Customer";
    private final DynamoDbClient dynamoDbClient;

    public CustomerRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void saveCustomer(Customer customer) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + customer.id()).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        item.put("name", AttributeValue.builder().s(customer.name()).build());
        item.put("phoneNumber", AttributeValue.builder().s(customer.phoneNumber()).build());

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(putItemRequest);
    }

}
