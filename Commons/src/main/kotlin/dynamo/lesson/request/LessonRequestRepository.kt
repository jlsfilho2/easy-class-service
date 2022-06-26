package dynamo.lesson.request

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dynamo.DynamoDBUtils
import dynamo.lesson.LessonUseCase
import models.lesson.request.LessonRequest
import models.lesson.request.LessonRequestStatus
import models.lesson.response.LessonRequestResponse
import requests.generateUUID

class LessonRequestRepository(
    private val mapper: ObjectMapper = jacksonObjectMapper()
) : LessonRequestService {

    override fun sendLessonRequest(requestBody: String) {
        val lessonRequest = DynamoDBUtils.objectMapper.readValue(requestBody, LessonRequest::class.java)
        lessonRequest.lessonRequestId = generateUUID()
        lessonRequest.status = LessonRequestStatus.PENDING.toString()
        DynamoDBUtils.mapper.save(lessonRequest)
    }

    override fun sendLessonRequestResponse(requestBody: String) {
        val lessonRequestResponse = mapper.readValue<LessonRequestResponse>(requestBody)
        when (lessonRequestResponse.lessonRequestStatus) {
            LessonRequestStatus.ACCEPTED.name -> {
                updateLessonRequestStatus(lessonRequestResponse)
                LessonUseCase.createLesson(lessonRequestResponse)
            }
            LessonRequestStatus.DENIED.name -> {
                updateLessonRequestStatus(lessonRequestResponse)
            }
            LessonRequestStatus.PENDING.name -> {
                /* LESSON REQUEST STATUS CAN ONLY BE SWITCHED TO PENDING ON CREATION  */
            }
        }
    }

    override fun updateLessonRequestStatus(lessonRequestResponse: LessonRequestResponse) {
        val updateItemRequest =
            makeUpdateLessonRequest(lessonRequestResponse.lessonRequestId, lessonRequestResponse.lessonRequestStatus)
        DynamoDBUtils.dynamoDB.updateItem(updateItemRequest)
    }

    override fun getLessonRequestById(lessonRequestId: String): String? {
        val lessonRequest = DynamoDBUtils.mapper.load(LessonRequest::class.java, lessonRequestId)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessonRequest)
    }

    override fun getLessonRequestByTeacherId(teacherId: String): String? {
        val eav = mapOf<String, AttributeValue>(
            ":teacherId" to AttributeValue().withS(teacherId)
        )
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("teacherId = :teacherId")
            .withExpressionAttributeValues(eav)
        val lessonRequest = getLessonRequestWithScanExpression(scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessonRequest)
    }

    override fun getLessonRequestByStudentId(studentId: String): String? {
        val eav = mapOf<String, AttributeValue>(
            ":studentId" to AttributeValue().withS(studentId)
        )
        val scanExpression = DynamoDBScanExpression()
            .withFilterExpression("studentId = :studentId")
            .withExpressionAttributeValues(eav)
        val lessonRequest = getLessonRequestWithScanExpression(scanExpression)
        return DynamoDBUtils.objectMapper.writeValueAsString(lessonRequest)
    }

    private fun getLessonRequestWithScanExpression(scanExpression: DynamoDBScanExpression): PaginatedScanList<LessonRequest>? {
        return DynamoDBUtils.mapper.scan(LessonRequest::class.java, scanExpression)
    }

    private fun makeUpdateLessonRequest(lessonRequestId: String, status: String): UpdateItemRequest {
        val tableName = "lessonrequest"
        val itemKey = mapOf<String, AttributeValue>(
            "lessonRequestId" to AttributeValue().withS(lessonRequestId)
        )
        val statusAttributeValue = AttributeValue().withS(status)
        val updatedValues = mutableMapOf<String, AttributeValueUpdate>(
            "status" to AttributeValueUpdate().withValue(statusAttributeValue)
        )
        return UpdateItemRequest(tableName, itemKey, updatedValues)
    }
}