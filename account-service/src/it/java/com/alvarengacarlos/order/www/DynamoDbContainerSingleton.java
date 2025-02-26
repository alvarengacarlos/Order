package com.alvarengacarlos.order.www;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class DynamoDbContainerSingleton {

    private static DynamoDbContainerSingleton instance;
    private GenericContainer dynamoDbContainer;
    private final Integer port = 8000;

    private DynamoDbContainerSingleton() {
    }

    static DynamoDbContainerSingleton getInstance() {
        if (instance == null) {
            instance = new DynamoDbContainerSingleton();
        }

        return instance;
    }

    public void startContainer() {
        dynamoDbContainer = new GenericContainer(DockerImageName.parse("amazon/dynamodb-local"))
                .withCommand("-jar DynamoDBLocal.jar -sharedDb -inMemory")
                .withExposedPorts(port);

        if (!dynamoDbContainer.isRunning()) {
            dynamoDbContainer.start();
        }
    }

    public Integer getPort() {
        if (dynamoDbContainer == null) {
            startContainer();
        }

        return dynamoDbContainer.getMappedPort(port);
    }
}
