package dynamo.lesson

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import dynamo.DynamoDBUtils
import models.lesson.Lesson
import models.lesson.request.LessonRequest
import models.lesson.request.LessonRequestStatus
import models.lesson.response.LessonRequestResponse
import requests.generateUUID

class LessonRepository : LessonService {

    override fun getLessonByTeacherId(teacherId: String): String? {
        val eav = makeExpressionAttributeValueForTeacherId(teacherId)
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("contains(teacher, :teacher)")
            .withExpressionAttributeValues(eav)
        val lessons: List<Lesson> = DynamoDBUtils.mapper.scan(Lesson::class.java, scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessons)
    }

    private fun makeExpressionAttributeValueForTeacherId(teacherId: String): Map<String, AttributeValue> {
        return mapOf<String, AttributeValue> (
            ":teacher" to AttributeValue().withS(teacherId)
        )
    }

}