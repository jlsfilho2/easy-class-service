package getteacher

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import models.Teacher
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
        .standard()
        .withRegion("sa-east-1")
        .build()
    private var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()

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
        val teachers: List<Teacher> = mapper.scan(Teacher::class.java, scanExpression)
        return objectMapper.writeValueAsString(teachers)
    }

    private fun getTeacherById(teacherId: String): String? {
        val teacher = mapper.load(Teacher::class.java, teacherId)
        return objectMapper.writeValueAsString(teacher)
    }
}
