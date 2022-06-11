package dynamo.lesson.request

import models.lesson.request.LessonRequest
import models.lesson.response.LessonRequestResponse

interface LessonRequestService {
    fun sendLessonRequest(requestBody: String)
    fun sendLessonRequestResponse(requestBody: String)
    fun updateLessonRequestStatus(lessonRequestResponse: LessonRequestResponse)
    fun getLessonRequestById(lessonRequestId: String): String?
    fun getLessonRequestByTeacherId(teacherId: String): String?
    fun getLessonRequestByStudentId(studentId: String): String?
}