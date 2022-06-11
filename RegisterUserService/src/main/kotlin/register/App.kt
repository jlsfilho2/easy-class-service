package register

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.user.register.RegisterUserRepository
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val registerRole = input?.queryStringParameters?.get("registerRole").orEmpty()

        RegisterUserRepository().saveUser(
            registerRole = registerRole,
            requestBody = input?.body,
        )

        return try {
            requestResponse(status = 200)
        } catch (exception: IOException) {
            requestResponse(status = 500)
        }
    }

}