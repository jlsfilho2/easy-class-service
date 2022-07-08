package app

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.lesson.request.LessonRequestInteractor
import requests.HttpVerb
import requests.requestResponse
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val interactor = LessonRequestInteractor()
        val teacherId = input?.queryStringParameters?.get("teacherId")
        val studentId = input?.queryStringParameters?.get("studentId")
        val lessonRequestId = input?.queryStringParameters?.get("lessonRequestId")

        val data = interactor.doGetLessonRequest(
            teacherId = teacherId,
            studentId = studentId,
            lessonRequestId = lessonRequestId
        )

        return try {
            requestResponse(
                data = data,
                status = 200,
                httpVerb = HttpVerb.GET
            )
        } catch (exception: IOException) {
            requestResponse(
                data = null,
                status = 500,
                httpVerb = HttpVerb.GET
            )
        }
    }

}