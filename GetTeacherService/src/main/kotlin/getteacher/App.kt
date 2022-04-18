package getteacher

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.DynamoDBUtils
import models.Teacher
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val teacherId = input?.queryStringParameters?.get("teacherId").orEmpty()

        val response = APIGatewayProxyResponseEvent()

        if (teacherId.isEmpty()) {
            response.withBody(getAllTeachers())
        } else {
            response.withBody(getTeacherById(teacherId))
        }

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }

    private fun getAllTeachers(): String? {
        val scanExpression = DynamoDBScanExpression()
        val teachers: List<Teacher> = DynamoDBUtils.mapper.scan(Teacher::class.java, scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(teachers)
    }

    private fun getTeacherById(teacherId: String): String? {
        val teacher = DynamoDBUtils.mapper.load(Teacher::class.java, teacherId)
        return DynamoDBUtils.objectMapper.writeValueAsString(teacher)
    }
}
