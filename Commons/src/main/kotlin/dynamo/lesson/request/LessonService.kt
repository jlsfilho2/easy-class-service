package dynamo.lesson.request

import models.lesson.Lesson

interface LessonService {
    fun sendLessonRequest(requestBody: String)
    fun sendLessonRequestResponse(requestBody: String)
    fun getLessonById(lessonId: String): String
    fun scanLessonWithId(lessonId: String): Lesson?
    fun getLessonByTeacherId(teacherId: String): String?
    fun updateLessonRequestStatus(status: String)
}