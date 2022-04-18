package saveteacher

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.DynamoDBUtils
import models.Teacher
import java.io.IOException
import java.util.UUID

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val teacher = DynamoDBUtils.objectMapper.readValue(input?.body, Teacher::class.java)
        teacher.teacherId = UUID.randomUUID().toString()
        DynamoDBUtils.mapper.save(teacher)

        val response = APIGatewayProxyResponseEvent()

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }

}