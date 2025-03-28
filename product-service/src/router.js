const crypto = require("crypto")
const express = require("express")
const { createProduct, deleteProduct, getProduct, listProducts } = require("./action")

const routerPrefix = "/products"

const router = express.Router()
router.post(routerPrefix, async (request, response, next) => {
    const callback = async (request, response) => {
        await createProduct({
            ...request.body,
            productId: crypto.randomUUID(),
            tenantId: request.headers["tenantid"],
        })
        response.status(201).json({ message: "Success" })
    }

    await execute(request, response, next, callback)
})

async function execute(request, response, next, callback) {
    try {
        await callback(request, response)
    } catch (error) {
        next(error)
    }
}

router.get(routerPrefix, async (request, response, next) => {
    const callback = async (request, response) => {
        const products = await listProducts({
            tenantId: request.headers["tenantid"],
        })
        response.status(200).json(products)
    }
    await execute(request, response, next, callback)
})

router.get(`${routerPrefix}/:productId`, async (request, response, next) => {
    const callback = async (request, response) => {
        const product = await getProduct({
            productId: request.params.productId,
        })
        if (!product) {
            return response.status(404).json({ message: "Product not found" })
        }
        response.status(200).json(product)
    }
    await execute(request, response, next, callback)
})

router.delete(`${routerPrefix}/:productId`, async (request, response, next) => {
    const callback = async (request, response) => {
        await deleteProduct({
            productId: request.params.productId,
        })
        response.status(204).json()
    }
    await execute(request, response, next, callback)
})

module.exports = router
