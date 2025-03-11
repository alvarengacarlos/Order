package com.alvarengacarlos.order.www;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class OrderRepositoryImpl implements OrderRepository {

    private final String tableName = "Order";
    private final String partitionKeyPrefix = "Order#";
    private final String sortKey = "Order";
    private final String customerIdIndex = "OrderOrderCustomerIdIndex";
    private final DynamoDbClient dynamoDbClient;

    public OrderRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void saveOrder(Order order) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + order.orderId).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        List<AttributeValue> productIds = order.productIds.stream().map((productId) -> AttributeValue.builder().s(productId.toString()).build()).toList();
        item.put("productIds", AttributeValue.builder().l(productIds).build());
        item.put("paymentMethod", AttributeValue.builder().s(order.paymentMethod.toString()).build());
        item.put("status", AttributeValue.builder().s(order.status.toString()).build());
        item.put("address", AttributeValue.builder().s(order.address).build());
        item.put("total", AttributeValue.builder().s(order.total.toString()).build());
        item.put("customerId", AttributeValue.builder().s(order.customerId.toString()).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public Order findOrder(UUID orderId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + orderId).build());
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
        List<UUID> productIds = item.get("productIds").l().stream().map((productId) -> UUID.fromString(productId.s())).toList();
        return new Order(
                UUID.fromString(item.get("partitionKey").s().replace(partitionKeyPrefix, "")),
                productIds,
                PaymentMethod.valueOf(item.get("paymentMethod").s()),
                OrderStatus.valueOf(item.get("status").s()),
                item.get("address").s(),
                new BigDecimal(item.get("total").s()),
                UUID.fromString(item.get("customerId").s())
        );
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + orderId).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(item)
                .updateExpression("SET #status = :status")
                .expressionAttributeValues(Map.of(
                        ":status", AttributeValue.builder().s(orderStatus.toString()).build()
                ))
                .expressionAttributeNames(Map.of("#status", "status"))
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public List<Order> findOrders(UUID customerId) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName(customerIdIndex)
                .keyConditionExpression("customerId = :customerId AND sortKey = :sortKey")
                .expressionAttributeValues(Map.of(
                        ":customerId", AttributeValue.builder().s(customerId.toString()).build(),
                        ":sortKey", AttributeValue.builder().s(sortKey).build()))
                .build();
        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        if (queryResponse.count() == 0) {
            return List.of();
        }

        return queryResponse.items().stream().map((item) -> {
            List<UUID> productIds = item.get("productIds").l().stream().map((productId) -> UUID.fromString(productId.s())).toList();
            return new Order(
                    UUID.fromString(item.get("partitionKey").s().replace(partitionKeyPrefix, "")),
                    productIds,
                    PaymentMethod.valueOf(item.get("paymentMethod").s()),
                    OrderStatus.valueOf(item.get("status").s()),
                    item.get("address").s(),
                    new BigDecimal(item.get("total").s()),
                    UUID.fromString(item.get("customerId").s())
            );
        }).toList();
    }

}
