package dynamo.lesson.request

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import dynamo.DynamoDBUtils
import models.lesson.Lesson
import models.lesson.LessonStatus
import models.lesson.request.LessonRequest
import models.lesson.response.LessonRequestResponse
import requests.generateUUID

class LessonRepository : LessonService {

    override fun sendLessonRequest(requestBody: String) {
        val lessonRequest = DynamoDBUtils.objectMapper.readValue(requestBody, LessonRequest::class.java)
        lessonRequest.lessonRequestId = generateUUID()
        lessonRequest.status = LessonStatus.PENDING.toString()
        DynamoDBUtils.mapper.save(lessonRequest)
    }

    override fun sendLessonRequestResponse(requestBody: String) {
        val deserializedRequestBody = requestBody to LessonRequestResponse()
        val lessonRequestResponse = deserializedRequestBody.second
        when (lessonRequestResponse.lessonResponseStatus) {
            LessonStatus.ACCEPTED.toString() -> {
                createLesson(lessonRequestResponse)
            }
            LessonStatus.CANCELED.toString() -> {
                updateLessonRequestStatus(
                    lessonRequestResponse.lessonId,
                    LessonStatus.CANCELED.toString()
                )
            }
            LessonStatus.DENIED.toString() -> {
                updateLessonRequestStatus(
                    lessonRequestResponse.lessonId,
                    LessonStatus.DENIED.toString()
                )
            }
            LessonStatus.CONCLUDED.toString() -> {
                updateLessonRequestStatus(lessonRequestResponse.lessonId,
                    LessonStatus.CONCLUDED.toString())
            }
            LessonStatus.PENDING.toString() -> {
                /* IS NOT POSSIBLE TO SET A REQUEST TO PENDING AGAIN */
            }
        }
    }

    override fun getLessonById(lessonId: String): String {
        val lesson = dynamo.lesson.scanLessonWithId(lessonId)
        return DynamoDBUtils.objectMapper.writeValueAsString(lesson)
    }

    override fun scanLessonWithId(lessonId: String): Lesson? {
        return DynamoDBUtils.mapper.load(Lesson::class.java, lessonId)
    }

    override fun getLessonByTeacherId(teacherId: String): String? {
        val eav = makeExpressionAttributeValueForTeacherId(teacherId)
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("contains(teacher, :teacher)")
            .withExpressionAttributeValues(eav)
        val lessons: List<Lesson> = DynamoDBUtils.mapper.scan(Lesson::class.java, scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessons)
    }

    override fun getLessonRequest(lessonRequestId: String): LessonRequest {
        return DynamoDBUtils.mapper.load(LessonRequest::class.java, lessonRequestId)
    }

    override fun updateLessonRequestStatus(lessonId: String, status: String) {
        val lesson = scanLessonWithId(lessonId)
        lesson?.status = status
        val putItemRequest = lesson?.let {
            makeUpdateLessonRequestStatusPutItemRequest(
                it.lessonId,
                status
            )
        }
        DynamoDBUtils.dynamoDB.putItem(putItemRequest)
    }

    private fun makeUpdateLessonRequestStatusPutItemRequest(lessonId: String, status: String): PutItemRequest {
        return PutItemRequest().apply {
            item = mapOf<String, AttributeValue>(
                "lessonId" to AttributeValue().withS(lessonId),
                "status" to AttributeValue().withS(status)
            )
        }
    }

    private fun makeExpressionAttributeValueForTeacherId(teacherId: String): Map<String, AttributeValue> {
        return mapOf<String, AttributeValue> (
            ":teacher" to AttributeValue().withS(teacherId)
        )
    }

    override fun createLesson(lessonRequestResponse: LessonRequestResponse) {
        val lessonRequest = getLessonRequest(lessonRequestResponse.lessonId)
        val lesson = Lesson.makeLessonFromLessonRequest(lessonRequest)
        saveLesson(lesson)
    }

    override fun saveLesson(lesson: Lesson) {
        DynamoDBUtils.mapper.save(lesson)
    }
}