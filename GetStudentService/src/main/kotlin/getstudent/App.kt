package getstudent

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import dynamo.DynamoDBUtils
import models.Student
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

    private fun getAllStudents(): String? {
        val scanExpression = DynamoDBScanExpression()
        val students: List<Student> = DynamoDBUtils.mapper.scan(Student::class.java, scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(students)
    }

    private fun getStudentById(teacherId: String): String? {
        val student = DynamoDBUtils.mapper.load(Student::class.java, teacherId)
        return DynamoDBUtils.objectMapper.writeValueAsString(student)
    }
}
