package app

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.teacher.getTeacher
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val subject = input?.queryStringParameters?.get("subject").orEmpty()
        val teacherId = input?.queryStringParameters?.get("teacherId").orEmpty()

        val data = getTeacher(teacherId, subject)

        return try {
            requestResponse(data = data.orEmpty(), status = 200)
        } catch (exception: IOException) {
            requestResponse(data = null, status = 500)
        }
    }
}
