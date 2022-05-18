package dynamo.lesson.request

interface LessonRequestService {
    fun sendLessonRequest(requestBody: String)
}