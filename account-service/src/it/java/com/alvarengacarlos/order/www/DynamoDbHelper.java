package com.alvarengacarlos.order.www;

import java.net.URI;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

public class DynamoDbHelper {

    private final DynamoDbClient dynamoDbClient;

    public DynamoDbHelper(Integer port) {
        this.dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:" + port))
                .build();
    }

    public DynamoDbClient getClient() {
        return dynamoDbClient;
    }

    public void createOrderTable() {
        String tableName = "Order";
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
}
