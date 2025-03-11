package com.alvarengacarlos.order.www;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class ShoppingCartRepositoryImpl implements ShoppingCartRepository {

    private final String tableName = "Order";
    private final String partitionKeyPrefix = "ShoppingCart#Account#";
    private final String sortKey = "ShoppingCart";
    private final DynamoDbClient dynamoDbClient;

    public ShoppingCartRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void addProduct(UUID customerId, UUID productId) {
        List<UUID> savedProductIds = getProducts(customerId);
        List<AttributeValue> productIds;
        if (savedProductIds.isEmpty()) {
            productIds = List.of(AttributeValue.builder().s(productId.toString()).build());
        } else {
            savedProductIds.add(productId);
            productIds = savedProductIds.stream().map((pId) -> AttributeValue.builder().s(pId.toString()).build()).toList();
        }

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + customerId).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        item.put("productIds", AttributeValue.builder().l(productIds).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    private List<UUID> getProducts(UUID customerId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + customerId).build());
        key.put("sortKey", AttributeValue.builder().s(sortKey).build());
        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        Map<String, AttributeValue> item = getItemResponse.item();
        if (item.isEmpty()) {
            return new LinkedList<>();
        }

        return item.get("productIds").l().stream().map((value) -> UUID.fromString(value.s())).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void removeProduct(UUID customerId, UUID productId) {
        List<UUID> savedProductIds = getProducts(customerId);
        if (savedProductIds.isEmpty()) {
            return;
        }

        savedProductIds.remove(productId);
        List<AttributeValue> productIds = savedProductIds.stream().map((pId) -> AttributeValue.builder().s(pId.toString()).build()).toList();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + customerId).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        item.put("productIds", AttributeValue.builder().l(productIds).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public List<UUID> findProducts(UUID customerId) {
        return Collections.unmodifiableList(getProducts(customerId));
    }

}
