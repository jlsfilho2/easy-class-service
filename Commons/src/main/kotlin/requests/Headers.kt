package requests

val getHeaders: Map<String, String> = mapOf(
    "Access-Control-Allow-Headers" to "*",
    "Access-Control-Allow-Origin" to "http://localhost:3000",
    "Access-Control-Allow-Methods" to "GET"
)

val postHeaders: Map<String, String> = mapOf(
    "Access-Control-Allow-Headers" to "*",
    "Access-Control-Allow-Origin" to "http://localhost:3000",
    "Access-Control-Allow-Methods" to "POST"
)

val putHeaders: Map<String, String> = mapOf(
    "Access-Control-Allow-Headers" to "*",
    "Access-Control-Allow-Origin" to "http://localhost:3000",
    "Access-Control-Allow-Methods" to "PUT"
)