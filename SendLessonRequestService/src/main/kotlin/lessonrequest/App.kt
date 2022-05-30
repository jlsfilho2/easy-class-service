package lessonrequest

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.lesson.request.LessonRepository
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val repository = LessonRepository()

        if (input?.body == null) {
            return requestResponse(data = "Your request body can't be empty", status = 400)
        } else {
            repository.sendLessonRequest(input.body)
        }

        return try {
            requestResponse(status = 200)
        } catch (exception: IOException) {
            requestResponse(status = 500)
        }
    }

}