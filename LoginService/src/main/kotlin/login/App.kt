package login

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.DynamoDBUtils
import dynamo.user.register.RegisterUserRepository
import models.auth.LoginRequest
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val loginRequest = DynamoDBUtils.objectMapper.readValue(input?.body, LoginRequest::class.java)
        val registerUserRepository = RegisterUserRepository()
        val data = registerUserRepository.getUserByRemoteId(loginRequest)

        return try {
            requestResponse(data = data, status = 200)
        } catch (exception: IOException) {
            requestResponse(data = null, status = 500)
        }
    }
}