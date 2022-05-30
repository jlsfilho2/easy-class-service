package dynamo.lesson.request

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import dynamo.DynamoDBUtils
import models.lesson.Lesson
import models.lesson.request.LessonRequest
import models.lesson.response.LessonRequestResponse
import requests.generateUUID

class LessonRepository : LessonService {

    override fun sendLessonRequest(requestBody: String) {
        val lessonRequest = DynamoDBUtils.objectMapper.readValue(requestBody, LessonRequest::class.java)
        lessonRequest.lessonRequestId = generateUUID()
        DynamoDBUtils.mapper.save(lessonRequest)
    }

    override fun sendLessonRequestResponse(requestBody: String) {
        val lessonRequestResponse = DynamoDBUtils.objectMapper.readValue(requestBody, LessonRequestResponse::class.java)
        val lesson = scanLessonWithId(lessonRequestResponse.lessonId)
        lesson?.status = lessonRequestResponse.lessonResponseStatus
        val request = PutItemRequest()
        request.item = mapOf<String, AttributeValue>(
            "lessonId" to AttributeValue().withS(lesson?.lessonId),
            "status" to AttributeValue().withS(lesson?.status)
        )
        DynamoDBUtils.dynamoDB.putItem(request)
    }

    override fun getLessonById(lessonId: String): String {
        val lesson = dynamo.lesson.scanLessonWithId(lessonId)
        return DynamoDBUtils.objectMapper.writeValueAsString(lesson)
    }

    override fun scanLessonWithId(lessonId: String): Lesson? {
        return DynamoDBUtils.mapper.load(Lesson::class.java, lessonId)
    }

    override fun getLessonByTeacherId(teacherId: String): String? {
        val eav = mapOf<String, AttributeValue> (
            ":teacher" to AttributeValue().withS(teacherId)
        )

        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("contains(teacher, :teacher)")
            .withExpressionAttributeValues(eav)
        val lessons: List<Lesson> = DynamoDBUtils.mapper.scan(Lesson::class.java, scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessons)
    }

}