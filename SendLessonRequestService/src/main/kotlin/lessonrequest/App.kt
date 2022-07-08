package lessonrequest

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.lesson.LessonRepository
import dynamo.lesson.request.LessonRequestRepository
import requests.HttpVerb
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val repository = LessonRequestRepository()

        if (input?.body != null) {
            repository.sendLessonRequest(input.body)
        } else {
            return requestResponse(
                data = "Your request body can't be empty",
                status = 400,
                httpVerb = HttpVerb.POST
            )
        }

        return try {
            requestResponse(status = 200, httpVerb = HttpVerb.POST)
        } catch (exception: IOException) {
            requestResponse(status = 500, httpVerb = HttpVerb.POST)
        }
    }

}