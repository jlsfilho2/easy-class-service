package dynamo.lesson.request

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dynamo.DynamoDBUtils
import dynamo.lesson.LessonUseCase
import models.lesson.request.LessonRequest
import models.lesson.request.LessonRequestStatus
import models.lesson.response.LessonRequestResponse
import requests.generateUUID

class LessonRequestRepository : LessonRequestService {

    override fun sendLessonRequest(requestBody: String) {
        val lessonRequest = DynamoDBUtils.objectMapper.readValue(requestBody, LessonRequest::class.java)
        lessonRequest.lessonRequestId = generateUUID()
        lessonRequest.status = LessonRequestStatus.PENDING.toString()
        DynamoDBUtils.mapper.save(lessonRequest)
    }

    override fun sendLessonRequestResponse(requestBody: String) {
        val mapper = jacksonObjectMapper()
        val lessonRequestResponse = mapper.readValue<LessonRequestResponse>(requestBody)
        when (lessonRequestResponse.lessonRequestStatus) {
            LessonRequestStatus.ACCEPTED.name -> {
                println("Entrou no accepted")
                updateLessonRequestStatus(lessonRequestResponse)
                LessonUseCase.createLesson(lessonRequestResponse)
            }
            LessonRequestStatus.DENIED.name -> {
                println("Entrou no denied")
                updateLessonRequestStatus(lessonRequestResponse)
            }
            LessonRequestStatus.PENDING.name -> {
                /* LESSON REQUEST STATUS CAN ONLY BE SWITCHED TO PENDING ON CREATION  */
            }
        }
    }

    override fun updateLessonRequestStatus(lessonRequestResponse: LessonRequestResponse) {
        val lessonRequest = getLessonRequest(lessonRequestResponse.lessonRequestId)
        lessonRequest.status = lessonRequestResponse.lessonRequestStatus
        val updateItemRequest = makeUpdateLessonRequest(lessonRequestResponse.lessonRequestId, lessonRequestResponse.lessonRequestStatus)
        DynamoDBUtils.dynamoDB.updateItem(updateItemRequest)
    }

    override fun getLessonRequest(lessonRequestId: String): LessonRequest {
        return DynamoDBUtils.mapper.load(LessonRequest::class.java, lessonRequestId)
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