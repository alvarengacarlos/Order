package com.alvarengacarlos.order.www;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final String tableName = "Order";
    private final String partitionKeyPrefix = "Account#";
    private final String sortKey = "Employee";
    private final String usernameIndex = "AccountEmployeeUsernameIndex";
    private final DynamoDbClient dynamoDbClient;

    public EmployeeRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void saveEmployee(Employee employee) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + employee.id).build());
        item.put("sortKey", AttributeValue.builder().s(sortKey).build());
        item.put("name", AttributeValue.builder().s(employee.name).build());
        item.put("username", AttributeValue.builder().s(employee.username).build());
        item.put("password", AttributeValue.builder().s(employee.passwordHash).build());
        item.put("salt", AttributeValue.builder().s(employee.salt).build());
        item.put("role", AttributeValue.builder().s(employee.employeeRole.toString()).build());
        item.put("isActive", AttributeValue.builder().bool(employee.isActive).build());
        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName(usernameIndex)
                .keyConditionExpression("username = :username AND sortKey = :sortKey")
                .expressionAttributeValues(Map.of(
                        ":username", AttributeValue.builder().s(username).build(),
                        ":sortKey", AttributeValue.builder().s(sortKey).build()))
                .build();
        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        if (queryResponse.count() == 0) {
            return null;
        }

        Map<String, AttributeValue> item = queryResponse.items().getFirst();

        return new Employee(
                UUID.fromString(item.get("partitionKey").s().replace(partitionKeyPrefix, "")),
                item.get("name").s(),
                item.get("username").s(),
                item.get("password").s(),
                item.get("salt").s(),
                EmployeeRole.valueOf(item.get("role").s()),
                item.get("isActive").bool()
        );
    }

    @Override
    public void deleteEmployee(UUID employeeId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + employeeId).build());
        key.put("sortKey", AttributeValue.builder().s(sortKey).build());
        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();
        dynamoDbClient.deleteItem(deleteItemRequest);
    }

    @Override
    public void updateIsActiveEmployeeAttribute(
            UUID employeeId,
            Boolean active
    ) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("partitionKey", AttributeValue.builder().s(partitionKeyPrefix + employeeId).build());
        key.put("sortKey", AttributeValue.builder().s(sortKey).build());

        Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();
        attributeUpdates.put("isActive", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().bool(active).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .attributeUpdates(attributeUpdates)
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }
}
