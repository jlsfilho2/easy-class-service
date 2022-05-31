package dynamo.lesson.request

import models.lesson.Lesson
import models.lesson.request.LessonRequest
import models.lesson.response.LessonRequestResponse

interface LessonService {
    fun sendLessonRequest(requestBody: String)
    fun sendLessonRequestResponse(requestBody: String)
    fun getLessonById(lessonId: String): String
    fun scanLessonWithId(lessonId: String): Lesson?
    fun getLessonByTeacherId(teacherId: String): String?
    fun updateLessonRequestStatus(lessonId: String, status: String)
    fun createLesson(lessonRequestResponse: LessonRequestResponse)
    fun saveLesson(lesson: Lesson)
    fun getLessonRequest(lessonRequestId: String): LessonRequest
}