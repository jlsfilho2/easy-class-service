package models.lesson.request

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "lessonrequest")
data class LessonRequest(
    @DynamoDBHashKey(attributeName = "lessonRequestId")
    var lessonRequestId: String = "",
    @DynamoDBAttribute(attributeName = "teacherId")
    var teacherId: String = "",
    @DynamoDBAttribute(attributeName = "studentId")
    var studentId: String = "",
    @DynamoDBAttribute(attributeName = "startDate")
    var startDate: String = "",
    @DynamoDBAttribute(attributeName = "endDate")
    var endDate: String = "",
    @DynamoDBAttribute(attributeName = "subject")
    var subject: String = "",
    @DynamoDBAttribute(attributeName = "hourlyPrice")
    var hourlyPrice: Int = 0,
    @DynamoDBAttribute(attributeName = "status")
    var status: String = ""
)