const crypto = require("crypto")
const { afterAll, beforeAll, describe, test, expect } = require("@jest/globals")
const supertest = require("supertest")
const { GenericContainer } = require("testcontainers")
const app = require("../../src/app")
const { createProduct } = require("../../src/action")
const { CreateTableCommand } = require("@aws-sdk/client-dynamodb")
const { getDynamoDbClient } = require("../../src/aws-client")

describe("Product Service", () => {
    const tenantId = crypto.randomUUID()

    let container = null
    beforeAll(async () => {
        container = await new GenericContainer("amazon/dynamodb-local")
            .withExposedPorts(8000)
            .start()

        process.env.DYNAMODB_PORT = container.getMappedPort(8000)
        const dynamoDbClient = getDynamoDbClient()
        const createTableCommand = new CreateTableCommand({
            TableName: "Order",
            AttributeDefinitions: [
                { AttributeName: "partitionKey", AttributeType: "S" },
                { AttributeName: "sortKey", AttributeType: "S" },
                { AttributeName: "tenantId", AttributeType: "S" },
            ],
            KeySchema: [
                { AttributeName: "partitionKey", KeyType: "HASH" },
                { AttributeName: "sortKey", KeyType: "RANGE" },
            ],
            ProvisionedThroughput: {
                ReadCapacityUnits: 1,
                WriteCapacityUnits: 1,
            },
            GlobalSecondaryIndexes: [
                {
                    IndexName: "ProductProductTenantIdIndex",
                    KeySchema: [
                        { AttributeName: "tenantId", KeyType: "HASH" },
                        { AttributeName: "sortKey", KeyType: "RANGE" },
                    ],
                    Projection: {
                        ProjectionType: "ALL",
                    },
                    ProvisionedThroughput: {
                        ReadCapacityUnits: 1,
                        WriteCapacityUnits: 1,
                    },
                },
            ],
        })
        await dynamoDbClient.send(createTableCommand)
    }, 30000)

    afterAll(async () => {
        await container.stop()
    }, 30000)

    describe("POST /product-service/products", () => {
        test("should return created http response", async () => {
            const response = await supertest(app)
                .post("/product-service/products")
                .send({
                    name: "Product 1",
                    description: "Product 1",
                    price: 100.10,
                })
                .set({ "tenantId": tenantId })

            expect(response.status).toEqual(201)
            expect(response.body).toEqual({ message: "Success" })
        })
    })

    describe("GET /product-service/products", () => {
        test("should return ok http response", async () => {
            const response = await supertest(app)
                .get("/product-service/products")
                .set({ "tenantId": tenantId })

            expect(response.status).toEqual(200)
        })
    })

    describe("GET /product-service/products/:productId", () => {
        test("should return not found http response", async () => {
            const productId = crypto.randomUUID()

            const response = await supertest(app)
                .get(`/product-service/products/${productId}`)

            expect(response.status).toEqual(404)
            expect(response.body).toEqual({ message: "Product not found" })
        })

        test("should return ok http response", async () => {
            const product = {
                productId: crypto.randomUUID(),
                name: "Product 1",
                description: "Product 1",
                price: 100.10,
            }
            await createProduct({
                ...product,
                tenantId: tenantId,
            })

            const response = await supertest(app)
                .get(`/product-service/products/${product.productId}`)

            expect(response.status).toEqual(200)
            expect(response.body).toEqual(product)
        })
    })

    describe("DELETE /product-service/products/:productId", () => {
        describe("should return no content http response", () => {
            test("when product does not exist", async () => {
                const productId = crypto.randomUUID()

                const response = await supertest(app)
                    .delete(`/product-service/products/${productId}`)

                expect(response.status).toEqual(204)
            })

            test("when product exists", async () => {
                const product = {
                    productId: crypto.randomUUID(),
                    name: "Product 1",
                    description: "Product 1",
                    price: 100.10,
                }
                await createProduct({
                    ...product,
                    tenantId: tenantId,
                })

                const response = await supertest(app)
                    .delete(`/product-service/products/${product.productId}`)

                expect(response.status).toEqual(204)
            })
        })
    })
})
