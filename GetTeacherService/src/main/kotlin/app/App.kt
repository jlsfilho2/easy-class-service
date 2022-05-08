package app

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import teacher.getTeacher
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val subjectId = input?.queryStringParameters?.get("subjectId").orEmpty()
        val isFeatured = input?.queryStringParameters?.get("isFeatured").orEmpty().toBoolean()
        val teacherId = input?.queryStringParameters?.get("teacherId").orEmpty()

        val data = getTeacher(teacherId, subjectId, isFeatured)

        return try {
            requestResponse(data = data.orEmpty(), status = 200)
        } catch (exception: IOException) {
            requestResponse(data = null, status = 500)
        }
    }


}
