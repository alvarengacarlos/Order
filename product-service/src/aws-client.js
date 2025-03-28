const { DynamoDBClient } = require("@aws-sdk/client-dynamodb")

let dynamoDbClient = null
function getDynamoDbClient() {
    let config = {}
    if (process.env.NODE_ENV === "test") {
        config = {
            endpoint: `http://localhost:${process.env.DYNAMODB_PORT}`,
            region: "local",
        }
    }
    if (!dynamoDbClient) {
        dynamoDbClient = new DynamoDBClient(config)
    }
    return dynamoDbClient
}

module.exports = { getDynamoDbClient }
