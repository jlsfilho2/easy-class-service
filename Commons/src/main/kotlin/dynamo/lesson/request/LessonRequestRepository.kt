package dynamo.lesson.request

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
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
        val deserializedRequestBody = requestBody to LessonRequestResponse()
        val lessonRequestResponse = deserializedRequestBody.second
        when (lessonRequestResponse.lessonRequestStatus) {
            LessonRequestStatus.ACCEPTED.toString() -> {
                updateLessonRequestStatus(lessonRequestResponse)
                LessonUseCase.createLesson(lessonRequestResponse)
            }
            LessonRequestStatus.DENIED.toString() -> {
                updateLessonRequestStatus(lessonRequestResponse)
            }
            LessonRequestStatus.PENDING.toString() -> {
                /* LESSON REQUEST STATUS CAN ONLY BE SWITCHED TO PENDING ON CREATION  */
            }
        }
    }

    override fun updateLessonRequestStatus(lessonRequestResponse: LessonRequestResponse) {
        val lessonRequest = getLessonRequest(lessonRequestResponse.lessonRequestId)
        lessonRequest.status = lessonRequestResponse.lessonRequestStatus
        val putItemRequest = makeUpdateLessonRequestStatusPutItemRequest(lessonRequestResponse.lessonRequestId, lessonRequestResponse.lessonRequestStatus)
        DynamoDBUtils.dynamoDB.putItem(putItemRequest)
    }

    override fun getLessonRequest(lessonRequestId: String): LessonRequest {
        return DynamoDBUtils.mapper.load(LessonRequest::class.java, lessonRequestId)
    }

    private fun makeUpdateLessonRequestStatusPutItemRequest(lessonRequestId: String, status: String): PutItemRequest {
        val putItemRequest = PutItemRequest()
        putItemRequest.item = mapOf<String, AttributeValue>(
            "lessonRequestId" to AttributeValue().withS(lessonRequestId),
            "status" to AttributeValue().withS(status))
        return putItemRequest
    }
}