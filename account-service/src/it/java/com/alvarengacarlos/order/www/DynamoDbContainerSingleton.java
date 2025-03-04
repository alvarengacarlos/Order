package com.alvarengacarlos.order.www;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class DynamoDbContainerSingleton {

    private static DynamoDbContainerSingleton instance;
    private final GenericContainer dynamoDbContainer;
    private final Integer port = 8000;

    private DynamoDbContainerSingleton() {
        dynamoDbContainer = new GenericContainer(DockerImageName.parse("amazon/dynamodb-local"))
                .withCommand("-jar DynamoDBLocal.jar -sharedDb -inMemory")
                .withExposedPorts(port);
    }

    static DynamoDbContainerSingleton getInstance() {
        if (instance == null) {
            instance = new DynamoDbContainerSingleton();
        }

        return instance;
    }

    public Integer startContainer() {
        dynamoDbContainer.start();
        return dynamoDbContainer.getMappedPort(port);
    }
}
