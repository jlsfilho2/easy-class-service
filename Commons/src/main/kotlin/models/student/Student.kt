package models.student

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "dynamo/student")
data class Student(
    @DynamoDBHashKey(attributeName = "studentId")
    var studentId: String = "",
    @DynamoDBAttribute(attributeName = "firstName")
    var firstName: String = "",
    @DynamoDBAttribute(attributeName = "lastName")
    var lastName: String = "",
    @DynamoDBAttribute(attributeName = "lessons")
    var lessons: List<String> = listOf(),
    @DynamoDBAttribute(attributeName = "rating")
    var rating: Int = 0,
)