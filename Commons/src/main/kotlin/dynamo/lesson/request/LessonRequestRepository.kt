package dynamo.lesson.request

import dynamo.DynamoDBUtils
import models.lesson.request.LessonRequest
import requests.generateUUID

class LessonRequestRepository : LessonRequestService {

    override fun sendLessonRequest(requestBody: String) {
        val lessonRequest = DynamoDBUtils.objectMapper.readValue(requestBody, LessonRequest::class.java)
        lessonRequest.lessonRequestId = generateUUID()
        DynamoDBUtils.mapper.save(lessonRequest)
    }

}