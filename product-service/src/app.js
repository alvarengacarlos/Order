const express = require("express")
const router = require("./router.js")

const app = express()
app.use(express.json())
app.use("/product-service", router)
app.use((error, request, response, next) => {
    console.error(error)
    response.status(500).json({ message: "Internal error" })
})

module.exports = app
