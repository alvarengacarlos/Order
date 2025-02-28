package com.alvarengacarlos.order.www;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

public class DynamoDbHelper {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "Order";

    public DynamoDbHelper(Integer port) {
        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:" + port))
                .build();
    }

    public DynamoDbClient getClient() {
        return dynamoDbClient;
    }

    public void createOrderTable() {
        ListTablesResponse res = dynamoDbClient.listTables();
        Boolean tableExists = res.tableNames().contains(tableName);
        if (tableExists) {
            return;
        }

        String partitionKeyName = "partitionKey";
        String sortKeyName = "sortKey";
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(partitionKeyName)
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                        AttributeDefinition.builder()
                                .attributeName(sortKeyName)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("username")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(partitionKeyName)
                        .keyType(KeyType.HASH)
                        .build(),
                        KeySchemaElement.builder()
                                .attributeName(sortKeyName)
                                .keyType(KeyType.RANGE)
                                .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .writeCapacityUnits(1L)
                        .readCapacityUnits(1L)
                        .build())
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName("AccountEmployeeUsernameIndex")
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("username")
                                .keyType(KeyType.HASH)
                                .build(),
                                KeySchemaElement.builder()
                                        .attributeName("sortKey")
                                        .keyType(KeyType.RANGE)
                                        .build())
                        .provisionedThroughput(ProvisionedThroughput.builder()
                                .writeCapacityUnits(1L)
                                .readCapacityUnits(1L)
                                .build())
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .build())
                .build();
        dynamoDbClient.createTable(createTableRequest);
    }

    public void truncateOrderTable() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        if (scanResponse.count() == 0) {
            return;
        }

        List<WriteRequest> writeRequests = scanResponse.items().stream().map(item -> {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("partitionKey", AttributeValue.builder().s(item.get("partitionKey").s()).build());
            key.put("sortKey", AttributeValue.builder().s(item.get("sortKey").s()).build());
            DeleteRequest deleteRequest = DeleteRequest.builder()
                    .key(key)
                    .build();
            return WriteRequest.builder().deleteRequest(deleteRequest).build();
        }).toList();

        BatchWriteItemRequest batchWriteItemRequest = BatchWriteItemRequest.builder()
                .requestItems(Map.of(tableName, writeRequests))
                .build();
        dynamoDbClient.batchWriteItem(batchWriteItemRequest);
    }
}
