package models

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "customer")
data class Teacher(
    @DynamoDBHashKey(attributeName = "teacherId")
    var teacherId: String = "",
    @DynamoDBAttribute(attributeName = "firstName")
    var firstName: String = "",
    @DynamoDBAttribute(attributeName = "lastName")
    var lastName: String = "",
    @DynamoDBAttribute(attributeName = "subjects")
    var subjects: List<String> = listOf(),
    @DynamoDBAttribute(attributeName = "lessons")
    var lessons: List<String> = listOf(),
    @DynamoDBAttribute(attributeName = "rating")
    var rating: Int = 0,
    @DynamoDBAttribute(attributeName = "availability")
    var availability: List<String> = listOf(),
)