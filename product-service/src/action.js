const { DeleteItemCommand, GetItemCommand, PutItemCommand, QueryCommand } = require("@aws-sdk/client-dynamodb")
const { getDynamoDbClient } = require("./aws-client")

const tableName = "Order"
const partitionKeyPrefix = "Product#"
const sortKey = "Product"
const tenantIdIndex = "ProductProductTenantIdIndex"

async function createProduct(dto) {
    //TODO: Implement validation
    const {
        productId,
        name,
        description,
        price,
        tenantId,
    } = dto

    const putItemCommand = new PutItemCommand({
        TableName: tableName,
        Item: {
            "partitionKey": { S: `${partitionKeyPrefix}${productId}` },
            "sortKey": { S: sortKey },
            "name": { S: name },
            "description": { S: description },
            "price": { N: price.toString() },
            "tenantId": { S: tenantId },
        }
    })
    const dynamoDbClient = getDynamoDbClient()
    await dynamoDbClient.send(putItemCommand)
}

async function listProducts(dto) {
    //TODO: Implement validation
    const { tenantId } = dto

    const queryCommand = new QueryCommand({
        TableName: tableName,
        IndexName: tenantIdIndex,
        KeyConditionExpression: "tenantId = :tenantId AND sortKey = :sortKey",
        ExpressionAttributeValues: {
            ":tenantId": { S: tenantId },
            ":sortKey": { S: sortKey },
        }
    })
    const dynamoDbClient = getDynamoDbClient()
    const output = await dynamoDbClient.send(queryCommand)

    return output.Items?.map(item => ({
        productId: item.partitionKey.S.replace(partitionKeyPrefix, ""),
        name: item.name.S,
        description: item.description.S,
        price: Number(item.price.N),
    })) ?? []
}

async function getProduct(dto) {
    //TODO: Implement validation
    const { productId } = dto

    const getItemCommand = new GetItemCommand({
        TableName: tableName,
        Key: {
            "partitionKey": { S: `${partitionKeyPrefix}${productId}` },
            "sortKey": { S: sortKey },
        }
    })
    const dynamoDbClient = getDynamoDbClient()
    const output = await dynamoDbClient.send(getItemCommand)
    if (!output.Item) {
        return null
    }

    return {
        productId: output.Item.partitionKey.S.replace(partitionKeyPrefix, ""),
        name: output.Item.name.S,
        description: output.Item.description.S,
        price: Number(output.Item.price.N),
    }
}

async function deleteProduct(dto) {
    //TODO: Implement validation
    const { productId } = dto

    const deleteItemCommand = new DeleteItemCommand({
        TableName: tableName,
        Key: {
            "partitionKey": { S: `${partitionKeyPrefix}${productId}` },
            "sortKey": { S: sortKey },
        }
    })
    const dynamoDbClient = getDynamoDbClient()
    await dynamoDbClient.send(deleteItemCommand)
}

module.exports = { createProduct, listProducts, getProduct, deleteProduct }
