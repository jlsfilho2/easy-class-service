package getstudent

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.DynamoDBUtils
import models.Lesson
import java.io.IOException

class App : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
        val studentId = input?.queryStringParameters?.get("studentId").orEmpty()
        val teacherId = input?.queryStringParameters?.get("teacherId").orEmpty()
        val response = APIGatewayProxyResponseEvent()
        val eav = mutableMapOf<String, AttributeValue>()
        if (!teacherId.isEmpty())
            eav.put(":teacherId", AttributeValue().withS(teacherId))
        if (!studentId.isEmpty()) {
            eav.put(":studentId", AttributeValue().withS(studentId))
        }
        response.withBody(getResult(eav))

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }


    fun getResult(map: Map<String, AttributeValue>): String? {
        val query: String = if (map.containsKey(":teacherId") && map.containsKey(":studentId"))
            "teacherId = :teacherId and studentId = :studentId" else if (
            map.containsKey(":teacherId")) "teacherId = :teacherId" else "studentId = :studentId"
        val queryExpression: DynamoDBQueryExpression<Lesson> = DynamoDBQueryExpression<Lesson>()
            .withKeyConditionExpression(query)
            .withExpressionAttributeValues(map.toMutableMap())
        val returnoDB = DynamoDBUtils.mapper.load(Lesson::class.java, queryExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(returnoDB)
    }
}




