package requests

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

fun requestResponse(
    data: String? = null,
    status: Int,
    httpVerb: HttpVerb
): APIGatewayProxyResponseEvent {
    return APIGatewayProxyResponseEvent()
        .withBody(data)
        .withHeaders(getVerbHeader(httpVerb = httpVerb))
        .withStatusCode(status)
}

private fun getVerbHeader(httpVerb: HttpVerb): Map<String, String> {
    return when (httpVerb) {
        HttpVerb.PUT -> { putHeaders }
        HttpVerb.POST -> { postHeaders }
        HttpVerb.GET -> { getHeaders }
    }
}