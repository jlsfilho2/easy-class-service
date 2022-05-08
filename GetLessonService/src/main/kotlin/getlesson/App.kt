package getstudent

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
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
        val after = input?.queryStringParameters?.get("after").orEmpty()
        val before = input?.queryStringParameters?.get("before").orEmpty()
        val studentId = input?.queryStringParameters?.get("studentId").orEmpty()
        val teacherId = input?.queryStringParameters?.get("teacherId").orEmpty()
        val response = APIGatewayProxyResponseEvent()
        val eav = mutableMapOf<String, AttributeValue>()
        if (!teacherId.isEmpty())
            eav.put(":teacherId", AttributeValue().withS(teacherId))
        if (!studentId.isEmpty()) {
            eav.put(":studentId", AttributeValue().withS(studentId))
        }
        if (!after.isEmpty())
            eav.put(":after", AttributeValue().withS(after))
        if (!before.isEmpty()) {
            eav.put(":before", AttributeValue().withS(before))
        }
        response.withBody(getResult(eav))

        return try {
            response.withStatusCode(200)
        } catch (exception: IOException) {
            response.withStatusCode(500)
        }
    }


    fun getResult(map: Map<String, AttributeValue>): String? {
        var query: String = if (map.containsKey(":teacherId") && map.containsKey(":studentId"))
            "teacherId = :teacherId and studentId = :studentId" else if (
            map.containsKey(":teacherId")) "teacherId = :teacherId" else "studentId = :studentId"
        query += if(map.containsKey(":after") && map.containsKey(":before"))
            " and timeslot >= :after and timeslot <= :before" else if (
                map.containsKey(":after")) " and timeslot >= :after" else " and timeslot <= :before"
        val queryExpression: DynamoDBScanExpression = DynamoDBScanExpression()
            .withFilterExpression(query)
            .withExpressionAttributeValues(map.toMutableMap())
        val returnoDB: List<Lesson> = DynamoDBUtils.mapper.scan(Lesson::class.java, queryExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(returnoDB)
    }



}




