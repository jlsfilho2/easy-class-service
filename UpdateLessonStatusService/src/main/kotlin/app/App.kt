package app

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.lesson.LessonRepository
import ext.isNotNull
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val repository = LessonRepository()

        if (input?.body?.isNotNull() == true) {
            repository.updateLessonStatus(input.body)
        } else {
            return requestResponse(data = "Your request body can't be empty", status = 400)
        }

        return try {
            requestResponse(status = 200)
        } catch (exception: IOException) {
            requestResponse(status = 500)
        }
    }
}