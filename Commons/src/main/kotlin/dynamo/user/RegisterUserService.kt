package dynamo.user

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

interface RegisterUserService {
    fun saveUser(requestBody: String?, registerRole: String)
}