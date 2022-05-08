package utils

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

fun requestResponse(data: String?, status: Int): APIGatewayProxyResponseEvent {
    return APIGatewayProxyResponseEvent()
        .withBody(data)
        .withHeaders(headers)
        .withStatusCode(status)
}