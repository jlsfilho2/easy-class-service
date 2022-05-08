import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import getfeaturedteacher.getFeaturedTeacher
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val subjectId = input?.queryStringParameters?.get("subjectId").orEmpty()
        val data = getFeaturedTeacher(subjectId)

        val response = APIGatewayProxyResponseEvent()
        response.withBody(data)

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }
}