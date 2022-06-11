package app

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.user.UserRepository
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val userRepository = UserRepository()
        val role = input?.queryStringParameters?.get("role").orEmpty()
        val subject = input?.queryStringParameters?.get("subject").orEmpty()
        val userId = input?.queryStringParameters?.get("userId").orEmpty()

        val data = userRepository.getUser(
            role = role,
            subject = subject,
            userId = userId
        )

        return try {
            requestResponse(data = data, status = 200)
        } catch (exception: IOException) {
            requestResponse(data = null, status = 500)
        }
    }
}