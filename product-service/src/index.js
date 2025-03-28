const ServerlessHttp = require("serverless-http")
const app = require("./app")

const h = ServerlessHttp(app)

module.exports.handler = async function (event, context) {
    return await h(event, context)
}
