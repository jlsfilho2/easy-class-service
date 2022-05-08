package app

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import student.getAllStudents
import student.getStudentById
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val studentId = input?.queryStringParameters?.get("studentId").orEmpty()

        val response = APIGatewayProxyResponseEvent()

        if (studentId.isEmpty()) {
            response.withBody(getAllStudents())
        } else {
            response.withBody(getStudentById(studentId))
        }

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }
}
